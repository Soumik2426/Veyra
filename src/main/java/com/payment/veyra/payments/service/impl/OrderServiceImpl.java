package com.payment.veyra.payments.service.impl;

import com.payment.veyra.common.enums.OrderStatus;
import com.payment.veyra.common.exceptions.BusinessRuleViolationException;
import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.mapper.OrderMapper;
import com.payment.veyra.mapper.PaymentMapper;
import com.payment.veyra.payments.dto.request.CreateOrderRequest;
import com.payment.veyra.payments.dto.response.OrderResponse;
import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import com.payment.veyra.payments.entity.Payment;
import com.payment.veyra.payments.repository.OrderRepository;
import com.payment.veyra.payments.repository.PaymentRepository;
import com.payment.veyra.payments.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final OrderMapper orderMapper;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes; // Default expiry time for orders in minutes

    @Override
    @Transactional
    public OrderResponse createOrder(UUID merchantId, CreateOrderRequest request) {
        if(request.receipt() != null && orderRepository.existsByMerchantIdAndReceipt(merchantId, request.receipt())){
            throw new ResourceNotFoundException("ORDER_RECEIPT_EXISTS", "order with receipt already exists: "+request.receipt());
        }

        OrderRecord newOrder = OrderRecord.builder()
                .receipt(request.receipt())
                .amount(request.amount())
                .notes(request.notes())
                .merchantId(merchantId)
                .orderStatus(OrderStatus.CREATED)
                .expiresAt(request.expiresAt() != null ? request.expiresAt()
                        : LocalDateTime.now().plusMinutes(defaultOrderExpiryMinutes))
                .build();

        newOrder = orderRepository.save(newOrder);

        //TODO: Publish Kafka event for order creation

        return orderMapper.toResponse(newOrder);
    }

    @Override
    public OrderResponse getOrderById(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + orderId));

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse cancelOrder(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + orderId));

        if (order.getOrderStatus() == OrderStatus.CANCELLED || order.getOrderStatus() == OrderStatus.PAID) {
            throw new BusinessRuleViolationException("ORDER CANNOT CANCEL", "Cannot cancel an order with status: " + order.getOrderStatus().name());
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order = orderRepository.save(order);

        return orderMapper.toResponse(order);
    }

    @Override
    public List<PaymentResponse> listPayments(UUID merchantId, UUID orderId) {
        OrderRecord order = orderRepository.findByIdAndMerchantId(orderId, merchantId)
                .orElseThrow(() -> new ResourceNotFoundException("ORDER_NOT_FOUND", "Order not found with id: " + orderId));

        List<Payment> paymentList =  paymentRepository.findByOrder_Id(order);
        return paymentList.stream()
                .map(payment-> paymentMapper.toResponse(payment))
                .collect(Collectors.toList());
    }
}
