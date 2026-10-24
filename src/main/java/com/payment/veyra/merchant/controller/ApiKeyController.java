package com.payment.veyra.merchant.controller;

import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.dto.response.ApiKeyResponse;
import com.payment.veyra.merchant.service.ApiKeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1/merchant/{merchantId}/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;

    //To create API key
    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@PathVariable UUID merchantId,
                                                             @Valid @RequestBody CreateApiKeyRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.generate(merchantId, request));
    }

    //To list all API keys for a merchant
    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listAllKeys(@PathVariable UUID merchantId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.listAllKeys(merchantId));
    }

    //To revoke an API key for a merchant
    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID merchantId, @PathVariable UUID apiKeyId){
        apiKeyService.revoke(merchantId, apiKeyId);
        return ResponseEntity.noContent().build();
    }

    //To rotate an existing API key for a merchant
    @PostMapping("/{apiKeyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable UUID merchantId, @PathVariable UUID apiKeyId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.rotateKey(merchantId, apiKeyId));
    }
}
