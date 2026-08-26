package com.payment.veyra.payments.dto.request;

import com.payment.veyra.common.entity.Money;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.Map;

public record CreateOrderRequest(

        @NotNull(message = "Amount is required")
        Money amount,

        @Size(max=100)
        String receipt, // orderId (known only to the merchant)

        Map<String, Object> notes,
        LocalDateTime expiresAt
) {
}
