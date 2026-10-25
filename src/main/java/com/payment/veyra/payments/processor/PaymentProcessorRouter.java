package com.payment.veyra.payments.processor;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.common.exceptions.BusinessRuleViolationException;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentProcessorRouter {
    private final Map<PaymentMethod, PaymentProcessor> paymentProcessors;

    public PaymentProcessorResponse charge(PaymentProcessorRequest request){
        PaymentProcessor processor = paymentProcessors.get(request.method());
        if(processor==null){
            throw new BusinessRuleViolationException("NO_PAYMENT_PROCESSOR", "No payment processor found for method: "+request.method());
        }
        return processor.charge(request);
    }
}
