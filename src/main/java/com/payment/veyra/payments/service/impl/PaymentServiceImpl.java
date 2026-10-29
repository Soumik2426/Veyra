package com.payment.veyra.payments.service.impl;

import com.payment.veyra.common.enums.OrderStatus;
import com.payment.veyra.common.enums.PaymentEvent;
import com.payment.veyra.common.enums.PaymentStatus;
import com.payment.veyra.common.exceptions.BusinessRuleViolationException;
import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.mapper.PaymentMapper;
import com.payment.veyra.payments.dto.request.PaymentInitRequest;
import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import com.payment.veyra.payments.entity.Payment;
import com.payment.veyra.payments.gateway.PaymentGatewayRouter;
import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;
import com.payment.veyra.payments.repository.OrderRepository;
import com.payment.veyra.payments.repository.PaymentRepository;
import com.payment.veyra.payments.service.PaymentService;
import com.payment.veyra.payments.statemachine.PaymentTransitionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;
    private final PaymentTransitionService paymentTransitionService;


    @Override
    @Transactional
    public PaymentResponse initiatePayment(UUID merchantId, PaymentInitRequest request) {
        OrderRecord order=orderRepository.findByIdAndMerchantId(request.orderId(), merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Order", request.orderId()));

        if(order.getOrderStatus()!= OrderStatus.CREATED && order.getOrderStatus()!= OrderStatus.ATTEMPTED){
            throw new BusinessRuleViolationException("ORDER_NOT_ELIGIBLE_FOR_PAYMENT", "Order cannot accept payment in status: "+order.getOrderStatus());
        }

        order.setOrderStatus(OrderStatus.ATTEMPTED);
        order.setAttempts(order.getAttempts()+1);


        Payment payment= Payment.builder()
                .order(order)
                .merchantId(merchantId)
                .amount(order.getAmount())
                .idempotencyKey(UUID.randomUUID().toString())
                .status(PaymentStatus.CREATED)
                .paymentMethod(request.method())
                .methodDetails(request.methodDetails())
                .build();
        payment=paymentRepository.save(payment);
        PaymentRequest paymentRequest=new PaymentRequest(payment.getId(),
                                                        request.orderId(),
                                                        merchantId,
                                                        order.getAmount(),
                                                        request.method(),
                                                        request.methodDetails()
                                                        );

        paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_ATTEMPT);
        PaymentResult result=paymentGatewayRouter.initiate(paymentRequest);

        if(result instanceof PaymentResult.Pending pending){
            payment.setProcessorReference(pending.registrationReference());
        }else if(result instanceof PaymentResult.Failure failure){
            //payment.setStatus(PaymentStatus.FAILED);
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
        }else if(result instanceof PaymentResult.Success success){
            log.warn("Invalid State");
            return null;
        }
        payment=paymentRepository.save(payment);
        orderRepository.save(order);
        //TODO: Publish Kafka event for payment initiation
        return paymentMapper.toResponse(payment);
    }

    @Override
    public PaymentResponse capturePayment(UUID merchantId, UUID paymentId) {
        Payment payment=paymentRepository.findByIdAndMerchantId(paymentId, merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("Payment", paymentId));

        //payment.setStatus(PaymentStatus.CAPTURED); //TODO: Payment StateMachine(DONE)
        paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);

        PaymentResult result=paymentGatewayRouter.capture(payment.getPaymentMethod(), paymentId);

        if(result instanceof PaymentResult.Success success){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
            payment.setCapturedAt(LocalDateTime.now());
            log.info("Payment capture successful for paymentId: {}", paymentId);
        }else if(result instanceof PaymentResult.Failure failure){
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAILURE);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
            log.warn("Payment capture failure for paymentId: {}", paymentId);
        }
        payment=paymentRepository.save(payment);
        //TODO: Publish Kafka event for payment capture
        return paymentMapper.toResponse(payment);
    }

    @Override
    @Transactional
    public void resolveAuthorization(UUID id, boolean approve, String bankRef, String errorCode, String errorDescription) {
        Payment payment=paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));

        if(payment.getStatus()!=PaymentStatus.AUTHORIZING){
            log.warn("Payment Status is not in Authorizing state, paymentId: {}, status: {}", id, payment.getStatus());
            return;
        }

        OrderRecord order=payment.getOrder();

        if(approve){
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_SUCCESS);
            payment.setBankReference(bankRef);
            payment.setAuthorizedAt(LocalDateTime.now());

            //Auto Capture after authorization
            paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_REQUEST);
            PaymentResult result=paymentGatewayRouter.capture(payment.getPaymentMethod(), payment.getId());
            if(result instanceof PaymentResult.Success success){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_SUCCESS);
                payment.setCapturedAt(LocalDateTime.now());
                order.setOrderStatus(OrderStatus.PAID);
                log.info("Payment captured successful for paymentId: {}", payment.getId());
            }else if(result instanceof PaymentResult.Failure failure){
                paymentTransitionService.apply(payment, PaymentEvent.CAPTURE_FAILURE);
                payment.setErrorCode(failure.errorCode());
                payment.setErrorDescription(failure.errorDescription());
                log.warn("Payment captured failure for paymentId: {}", payment.getId());
            }

        }else{
            paymentTransitionService.apply(payment, PaymentEvent.AUTHORIZE_FAILURE);
            payment.setErrorCode(errorCode);
            payment.setErrorDescription(errorDescription);
            log.warn("Payment authorization failed for paymentId: {}, errorCode: {}, errorDescription: {}", payment.getId(), errorCode, errorDescription);
        }


        paymentRepository.save(payment);
        orderRepository.save(order);

        //TODO: Send an outbox(kafka event)
    }
}
