package com.payment.veyra.payments.dto.request;

import com.payment.veyra.common.enums.PaymentMethod;
import jakarta.validation.constraints.NotNull;

import java.util.Map;
import java.util.UUID;

public record PaymentInitRequest(
        @NotNull(message = "Order ID cannot be null")
        UUID orderId,

        @NotNull(message = "Payment method cannot be null")
        PaymentMethod method,

        Map<String, Object> methodDetails
) {
}
