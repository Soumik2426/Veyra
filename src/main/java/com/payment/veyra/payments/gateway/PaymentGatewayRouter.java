package com.payment.veyra.payments.gateway;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentGatewayRouter {
    private final Map<PaymentMethod, PaymentAdapter> paymentAdapters;

    public PaymentResult initiate(PaymentRequest request){
        PaymentAdapter adapter = paymentAdapters.get(request.method());
        if(adapter==null){
            throw new IllegalArgumentException("No payment adapter found for method: "+request.method());
        }
        return adapter.initiate(request);
    }
}
