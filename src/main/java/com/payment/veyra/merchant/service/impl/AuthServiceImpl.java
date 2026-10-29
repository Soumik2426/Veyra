package com.payment.veyra.merchant.service.impl;

import com.payment.veyra.common.enums.MerchantStatus;
import com.payment.veyra.common.enums.UserRole;
import com.payment.veyra.common.exceptions.DuplicateResourceException;
import com.payment.veyra.common.exceptions.ResourceNotFoundException;
import com.payment.veyra.mapper.MerchantMapper;
import com.payment.veyra.merchant.dto.request.MerchantLoginRequest;
import com.payment.veyra.merchant.dto.request.MerchantSignupRequest;
import com.payment.veyra.merchant.dto.response.MerchantLoginResponse;
import com.payment.veyra.merchant.dto.response.MerchantResponse;
import com.payment.veyra.merchant.entity.AppUser;
import com.payment.veyra.merchant.entity.Merchant;
import com.payment.veyra.merchant.repository.AppUserRepository;
import com.payment.veyra.merchant.repository.MerchantRepository;
import com.payment.veyra.merchant.security.JwtUtil;
import com.payment.veyra.merchant.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

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
                .passwordHash(passwordEncoder.encode(request.password()))
                .role(UserRole.OWNER)
                .build();
        appUserRepository.save(appUser);
        return merchantMapper.toResponse(merchant);
    }


    @Override
    public MerchantLoginResponse login(MerchantLoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        AppUser user= (AppUser) authentication.getPrincipal();

        String token=jwtUtil.generateAccessToken(
                user.getEmail(),
                user.getMerchant().getId(),
                user.getRole().name()
        );
        return new MerchantLoginResponse(token);
    }
}
