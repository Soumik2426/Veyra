package com.payment.veyra.merchant.service;

import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.dto.response.ApiKeyResponse;

import java.util.List;
import java.util.UUID;

public interface ApiKeyService {
    ApiKeyCreateResponse generate(UUID merchantId, CreateApiKeyRequest request);

    List<ApiKeyResponse> listAllKeys(UUID merchantId);

    void revoke(UUID merchantId, UUID apiKeyId);

    ApiKeyCreateResponse rotateKey(UUID merchantId, UUID apiKeyId);
}
