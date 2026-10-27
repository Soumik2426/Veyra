package com.payment.veyra.vault.service;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import com.payment.veyra.vault.dto.request.TokenizeRequest;
import com.payment.veyra.vault.dto.response.TokenizeResponse;
import jakarta.validation.Valid;

import java.util.Map;
import java.util.UUID;

public interface CardTokenService {
    TokenizeResponse tokenize(@Valid TokenizeRequest request, UUID merchantId);

    PaymentProcessorResponse charge(UUID PaymentId, String token, Money amount, PaymentMethod method, Map<String, Object> methodDetails);
}
