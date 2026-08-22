package com.payment.veyra.merchant.service.impl;

import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.entity.ApiKey;
import com.payment.veyra.merchant.entity.Merchant;
import com.payment.veyra.merchant.repository.ApiKeyRepository;
import com.payment.veyra.merchant.repository.MerchantRepository;
import com.payment.veyra.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApiKeyServiceImpl implements ApiKeyService {
    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;


    @Override
    public ApiKeyCreateResponse generate(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "vey_"+request.environment().name().toUpperCase()+"big_random_string";
        String keySecretHash = "big_random_string"; //TODO: replace with cryptographic hex

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .KeySecretHash(keySecretHash) //TODO: encode with BcryptPasswordEncoder
                .environment(request.environment())
                .build();

        apiKey= apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(
                apiKey.getId(),
                keyId,
                keySecretHash, // Store the secret hash in the database, but return the plain secret to the user only once
                apiKey.getEnvironment()
        );
    }
}
