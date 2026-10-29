package com.payment.veyra.merchant.service;

import com.payment.veyra.merchant.dto.request.MerchantLoginRequest;
import com.payment.veyra.merchant.dto.request.MerchantSignupRequest;
import com.payment.veyra.merchant.dto.response.MerchantLoginResponse;
import com.payment.veyra.merchant.dto.response.MerchantResponse;
import jakarta.validation.Valid;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);

    MerchantLoginResponse login(MerchantLoginRequest request);
}
