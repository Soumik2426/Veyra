package com.payment.veyra.merchant.service.impl;

import com.payment.veyra.common.enums.MerchantStatus;
import com.payment.veyra.common.enums.UserRole;
import com.payment.veyra.common.exceptions.DuplicateResourceException;
import com.payment.veyra.mapper.MerchantMapper;
import com.payment.veyra.merchant.dto.request.MerchantSignupRequest;
import com.payment.veyra.merchant.dto.response.MerchantResponse;
import com.payment.veyra.merchant.entity.AppUser;
import com.payment.veyra.merchant.entity.Merchant;
import com.payment.veyra.merchant.repository.AppUserRepository;
import com.payment.veyra.merchant.repository.MerchantRepository;
import com.payment.veyra.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    private final AppUserRepository appUserRepository;
    private final MerchantRepository merchantRepository;
    private final MerchantMapper merchantMapper;

    @Override
    @Transactional
    public MerchantResponse signup(MerchantSignupRequest request) {
        if(merchantRepository.existsByEmail(request.email())){
            throw new DuplicateResourceException("DUPLICATE_EMAIL","Email already exists");
        }

        //Creating the Merchant
        Merchant merchant = merchantMapper.toMerchantEntity(request);
        merchant.setStatus(MerchantStatus.PENDING_KYC);
        merchant=merchantRepository.save(merchant);

        //Creating the first AppUser(as soon as a merchant is created, an app user has to be created for the merchant)
        AppUser appUser = AppUser.builder()
                .email(request.email())
                .merchant(merchant)
                .passwordHash(request.password()) //TODO: Hash the password
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);
        return merchantMapper.toResponse(merchant);
    }
}
