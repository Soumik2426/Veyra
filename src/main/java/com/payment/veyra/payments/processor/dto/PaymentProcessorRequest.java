package com.payment.veyra.payments.processor.dto;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.PaymentMethod;

import java.util.Map;
import java.util.UUID;

public record PaymentProcessorRequest(
        UUID processingId,
        UUID paymentId,
        PaymentMethod method,
        Money amount,
        String pan,
        String expiry,
        Map<String, Object> methodDetails
) {

    public static PaymentProcessorRequest card(UUID paymentId, PaymentMethod method, Money amount, String pan, String expiry, Map<String, Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, method, amount, pan, expiry, methodDetails);
    }

    public static PaymentProcessorRequest nonCard(UUID paymentId, PaymentMethod method, Money amount, Map<String, Object> methodDetails){
        return new PaymentProcessorRequest(UUID.randomUUID(), paymentId, method, amount, null, null, methodDetails);
    }
}
