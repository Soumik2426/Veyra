package com.payment.veyra.payments.processor.dto;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.PaymentMethod;

import java.util.Map;

public record PaymentProcessorRequest(
        PaymentMethod method,
        Money amount,
        Map<String, Object> methodDetails
) {
}
