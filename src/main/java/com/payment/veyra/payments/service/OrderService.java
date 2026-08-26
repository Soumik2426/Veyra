package com.payment.veyra.payments.service;

import com.payment.veyra.payments.dto.request.CreateOrderRequest;
import com.payment.veyra.payments.dto.response.OrderResponse;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(UUID merchantId, CreateOrderRequest request);
}
