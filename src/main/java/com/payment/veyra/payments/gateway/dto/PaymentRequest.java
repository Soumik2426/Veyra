package com.payment.veyra.payments.gateway.dto;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentRequest(
        UUID PaymentId,
        UUID OrderId,
        UUID merchantId,
        Money amount,
        PaymentMethod method,
        Map<String, Object> methodDetails
) {
}
