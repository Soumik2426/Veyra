package com.payment.veyra.merchant.controller;

import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.dto.response.ApiKeyResponse;
import com.payment.veyra.merchant.security.MerchantContext;
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
@RequestMapping("/v1/merchant/api-keys")
@RequiredArgsConstructor
public class ApiKeyController {

    private final ApiKeyService apiKeyService;
    private final MerchantContext merchantContext;

    //To create API key
    @PostMapping
    public ResponseEntity<ApiKeyCreateResponse> createApiKey(@Valid @RequestBody CreateApiKeyRequest request){
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiKeyService.createApiKey(merchantContext.getMerchantId(), request));
    }

    //To list all API keys for a merchant
    @GetMapping
    public ResponseEntity<List<ApiKeyResponse>> listAllKeys(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.listAllKeys(merchantContext.getMerchantId()));
    }

    //To revoke an API key for a merchant
    @DeleteMapping("/{apiKeyId}")
    public ResponseEntity<Void> revoke(@PathVariable UUID apiKeyId){
        apiKeyService.revoke(merchantContext.getMerchantId(), apiKeyId);
        return ResponseEntity.noContent().build();
    }

    //To rotate an existing API key for a merchant
    @PostMapping("/{apiKeyId}/rotate")
    public ResponseEntity<ApiKeyCreateResponse> rotateKey(@PathVariable UUID apiKeyId){
        return ResponseEntity.status(HttpStatus.OK)
                .body(apiKeyService.rotateKey(merchantContext.getMerchantId(), apiKeyId));
    }
}
