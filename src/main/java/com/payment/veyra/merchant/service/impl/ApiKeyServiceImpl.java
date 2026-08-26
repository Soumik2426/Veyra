package com.payment.veyra.merchant.service.impl;

import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.merchant.dto.request.CreateApiKeyRequest;
import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.dto.response.ApiKeyResponse;
import com.payment.veyra.merchant.entity.ApiKey;
import com.payment.veyra.merchant.entity.Merchant;
import com.payment.veyra.merchant.repository.ApiKeyRepository;
import com.payment.veyra.merchant.repository.MerchantRepository;
import com.payment.veyra.merchant.service.ApiKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ApiKeyServiceImpl implements ApiKeyService {
    private final MerchantRepository merchantRepository;
    private final ApiKeyRepository apiKeyRepository;


    @Override
    @Transactional
    public ApiKeyCreateResponse generate(UUID merchantId, CreateApiKeyRequest request) {
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(()-> new ResourceNotFoundException("merchant", merchantId));

        String keyId = "vey_"+request.environment().name().toLowerCase()+"_"+ RandomizerUtil.randomBase64(25);
        String keySecretHash = RandomizerUtil.randomBase64(40);

        ApiKey apiKey = ApiKey.builder()
                .merchant(merchant)
                .keyId(keyId)
                .keySecretHash(keySecretHash) //TODO: encode with BcryptPasswordEncoder
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

    @Override
    public List<ApiKeyResponse> list(UUID merchantId) {
        return apiKeyRepository.findByMerchant_Id(merchantId).stream()
                .map(apiKey ->
                        new ApiKeyResponse(apiKey.getId(),
                                apiKey.getKeyId(),
                                apiKey.getEnvironment(),
                                apiKey.isEnabled(),
                                apiKey.getLastUsedAt(),null))
                .toList();
    }

    @Override
    @Transactional
    public void revoke(UUID merchantId, UUID apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("apiKey", apiKeyId));

        apiKey.setEnabled(false);
        apiKeyRepository.save(apiKey);
    }

    @Override
    @Transactional
    public ApiKeyCreateResponse rotateKey(UUID merchantId, UUID apiKeyId) {
        ApiKey apiKey = apiKeyRepository.findById(apiKeyId)
                .filter(k -> k.getMerchant().getId().equals(merchantId))
                .orElseThrow(() -> new ResourceNotFoundException("apiKey", apiKeyId));

        String newRawSecret = RandomizerUtil.randomBase64(40);

        apiKey.setPreviousKeySecretHash(apiKey.getKeySecretHash());
        apiKey.setKeySecretHash(newRawSecret); //TODO: encode with BcryptPassword
        apiKey.setRotatedAt(LocalDateTime.now());
        apiKey.setGracePeriodExpiryAt(LocalDateTime.now().plusHours(24));

        apiKey = apiKeyRepository.save(apiKey);

        return new ApiKeyCreateResponse(
                apiKey.getId(),
                apiKey.getKeyId(),
                newRawSecret,
                apiKey.getEnvironment()
        );
    }
}
