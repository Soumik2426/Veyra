package com.payment.veyra.payments.gateway;

import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest request);
}
