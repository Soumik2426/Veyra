package com.payment.veyra.merchant.service;

import com.payment.veyra.merchant.dto.request.MerchantSignupRequest;
import com.payment.veyra.merchant.dto.response.MerchantResponse;

public interface AuthService {
    MerchantResponse signup(MerchantSignupRequest request);
}
