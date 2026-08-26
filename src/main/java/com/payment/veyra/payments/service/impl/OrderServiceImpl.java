package com.payment.veyra.payments.service.impl;

import com.payment.veyra.common.enums.OrderStatus;
import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.payments.dto.request.CreateOrderRequest;
import com.payment.veyra.payments.dto.response.OrderResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import com.payment.veyra.payments.repository.OrderRepository;
import com.payment.veyra.payments.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    @Value("${payment.order.default-order-expiry-minutes:30}")
    private int defaultOrderExpiryMinutes; // Default expiry time for orders in minutes

    @Override
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

        return new OrderResponse(
                newOrder.getId(),
                newOrder.getMerchantId(),
                newOrder.getReceipt(),
                newOrder.getAmount(),
                newOrder.getOrderStatus(),
                newOrder.getAttempts(),
                newOrder.getNotes(),
                newOrder.getExpiresAt(),
                null);
    }
}
