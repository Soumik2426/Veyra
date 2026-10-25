package com.payment.veyra.payments.processor.strategy;

import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;

public class UPIPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        return null;
    }
}
