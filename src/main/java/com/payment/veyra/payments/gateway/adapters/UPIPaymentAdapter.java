package com.payment.veyra.payments.gateway.adapters;

import com.payment.veyra.payments.gateway.PaymentAdapter;
import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;

public class UPIPaymentAdapter implements PaymentAdapter {
    @Override
    public PaymentResult initiate(PaymentRequest request){
        return null;
    }
}
