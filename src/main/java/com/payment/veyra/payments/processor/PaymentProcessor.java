package com.payment.veyra.payments.processor;

import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;

public interface PaymentProcessor {
    PaymentProcessorResponse charge(PaymentProcessorRequest request);
}
