package com.payment.veyra.payments.service;

import com.payment.veyra.payments.dto.request.PaymentInitRequest;
import com.payment.veyra.payments.dto.response.PaymentResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface PaymentService {
    PaymentResponse initiatePayment(UUID merchantId, @Valid PaymentInitRequest request);

    PaymentResponse capturePayment(UUID merchantId, UUID paymentId);
}
