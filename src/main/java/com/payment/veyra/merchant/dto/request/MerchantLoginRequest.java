package com.payment.veyra.merchant.dto.request;

public record MerchantLoginRequest(
        String email,
        String password
) {
}
