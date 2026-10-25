package com.payment.veyra.payments.service.impl;

import com.payment.veyra.common.enums.OrderStatus;
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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentGatewayRouter paymentGatewayRouter;
    private final PaymentMapper paymentMapper;


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

        PaymentResult result=paymentGatewayRouter.initiate(paymentRequest);

        if(result instanceof PaymentResult.Pending pending){
            payment.setProcessorReference(pending.registrationReference());
        }else if(result instanceof PaymentResult.Failure failure){
            payment.setStatus(PaymentStatus.FAILED);
            payment.setErrorCode(failure.errorCode());
            payment.setErrorDescription(failure.errorDescription());
        }

        payment=paymentRepository.save(payment);
        orderRepository.save(order);

        return paymentMapper.toResponse(payment);
    }
}
