package com.payment.veyra.vault.service.impl;

import com.payment.veyra.common.entity.Money;
import com.payment.veyra.common.enums.CardBrand;
import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.PaymentProcessorRouter;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import com.payment.veyra.vault.config.VaultEncryptionConfig;
import com.payment.veyra.vault.dto.request.TokenizeRequest;
import com.payment.veyra.vault.dto.response.TokenizeResponse;
import com.payment.veyra.vault.entity.CardToken;
import com.payment.veyra.vault.entity.VaultCard;
import com.payment.veyra.vault.repository.CardTokenRepository;
import com.payment.veyra.vault.repository.VaultCardRepository;
import com.payment.veyra.vault.service.CardTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.encrypt.BytesEncryptor;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import javax.crypto.KeyGenerator;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CardTokenServiceImpl implements CardTokenService {
    private final CardTokenRepository cardTokenRepository;
    private final VaultCardRepository vaultCardRepository;
    private final BytesEncryptor dekEncryptor;
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    @Transactional
    public TokenizeResponse tokenize(TokenizeRequest request, UUID merchantId) {
        log.info("Tokenizing card for merchant: {}", merchantId);
        String lastFourDigits=request.pan().substring(request.pan().length()-4);
        String bin=request.pan().substring(0,6);
        CardBrand cardName=DetectCardBrand(request.pan());

        byte[] dek= KeyGenerators.secureRandom(32).generateKey();
        byte[] encryptedPan=VaultEncryptionConfig.panEncryptor(dek)
                .encrypt(request.pan().getBytes(StandardCharsets.UTF_8));
        byte[] encryptedDek=dekEncryptor.encrypt(dek);

        VaultCard vaultCard=VaultCard.builder()
                .lastFour(lastFourDigits)
                .bin(bin)
                .encryptedPan(encryptedPan)
                .encryptedDek(encryptedDek)
                .brand(cardName)
                .expiryMonth(request.expiryMonth().toString())
                .expiryYear(request.expiryYear().toString())
                .cardHolderName(request.cardholderName())
                .build();

        vaultCard=vaultCardRepository.save(vaultCard);
        String token="tok_"+ RandomizerUtil.randomBase64(32);

        CardToken cardToken=CardToken.builder()
                .token(token)
                .vaultCard(vaultCard)
                .customer(request.customerId())
                .merchant(merchantId)
                .build();

        cardToken=cardTokenRepository.save(cardToken);
        return new TokenizeResponse(cardToken.getToken(),
                vaultCard.getLastFour(),
                vaultCard.getBrand(),
                request.expiryMonth(),
                request.expiryYear()
        );
    }

    @Override
    public PaymentProcessorResponse charge(UUID PaymentId, String token, Money amount, PaymentMethod method, Map<String, Object> methodDetails) {
        CardToken cardToken = cardTokenRepository.findByTokenAndRevokedAtIsNull(token)
                .orElseThrow(()-> new ResourceNotFoundException("CardToken", token));

        VaultCard vaultCard = cardToken.getVaultCard();
        byte[] panBytes=null;

        try {
            byte[] dek = dekEncryptor.decrypt(vaultCard.getEncryptedDek());
            panBytes = VaultEncryptionConfig.panEncryptor(dek).decrypt(vaultCard.getEncryptedPan());

            String pan = new String(panBytes, StandardCharsets.UTF_8);
            String expiry = vaultCard.getExpiryMonth() + "/" + vaultCard.getExpiryYear();

            PaymentProcessorRequest request = PaymentProcessorRequest.card(
                    PaymentId,
                    method,
                    amount,
                    pan,
                    expiry,
                    methodDetails
            );

            PaymentProcessorResponse response = paymentProcessorRouter.charge(request);
            log.info("Vault charge registered, token={}****", token.substring(0, 4));
            return response;
        }catch (Exception e){
            log.warn("Vault charge failed, token={}****", token.substring(0, 4));
            return new PaymentProcessorResponse.Failure("VAULT_CHARGE_FAILED", e.getMessage());
        }finally {
            if(panBytes!=null){
                Arrays.fill(panBytes, (byte) 0);
            }
        }
    }

    private CardBrand DetectCardBrand(String pan) {
        if(pan.startsWith("4")){
            return CardBrand.VISA;
        } else if(pan.startsWith("5") || pan.startsWith("2")){
            return CardBrand.MASTERCARD;
        } else if(pan.startsWith("37") || pan.startsWith("34")){
            return CardBrand.AMEX;
        } else{
            return CardBrand.RUPAY;
        }
    }
}
