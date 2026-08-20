package com.payment.veyra.merchant.dto.request;

import com.payment.veyra.common.enums.BusinessType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MerchantSignupRequest (
    @NotNull(message = "Name is required")
    @Size(max = 50, message = "Name must be less than or equal to 50 characters")
    String name,

    @Email
    @NotNull(message = "Email is required")
    String email,

    @NotNull(message = "Password is required")
    @Size(min=8, message = "Password must be at least 8 characters long")
    String password,

    @Size(max=50, message = "Business name must be less than or equal to 50 characters")
    String businessName,

    BusinessType businessType
){
}
