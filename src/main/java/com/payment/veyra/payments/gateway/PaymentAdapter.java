package com.payment.veyra.payments.gateway;

import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.util.UUID;

public interface PaymentAdapter {
    PaymentResult initiate(PaymentRequest request);

    PaymentResult capture(UUID paymentId);
}
