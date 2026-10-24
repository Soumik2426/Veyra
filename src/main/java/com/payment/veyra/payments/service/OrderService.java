package com.payment.veyra.payments.service;

import com.payment.veyra.payments.dto.request.CreateOrderRequest;
import com.payment.veyra.payments.dto.response.OrderResponse;
import com.payment.veyra.payments.dto.response.PaymentResponse;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID merchantId, CreateOrderRequest request);

    OrderResponse getOrderById(UUID merchantId, UUID orderId);

    OrderResponse cancelOrder(UUID merchantId, UUID orderId);

    List<PaymentResponse> listPayments(UUID merchantId, UUID orderId);

}
