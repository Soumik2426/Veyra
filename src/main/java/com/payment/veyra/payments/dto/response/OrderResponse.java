package com.payment.veyra.payments.dto.response;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

public record OrderResponse(
        UUID id, //This is the OrderId which is Known to our System(Veyra)
        UUID merchantId,
        String receipt,
        Money amount,
        OrderStatus status,
        Integer attempts,
        Map<String, Object> notes,
        LocalDateTime expiresAt,
        LocalDateTime createdAt
) {
}
