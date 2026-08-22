package com.payment.veyra.merchant.service;

import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import jakarta.validation.Valid;

import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse generate(UUID merchantId, CreateApiKeyRequest request);
}
