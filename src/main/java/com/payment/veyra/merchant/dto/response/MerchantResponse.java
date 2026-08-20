package com.payment.veyra.merchant.dto.response;

import com.payment.veyra.common.enums.BusinessType;
import com.payment.veyra.common.enums.MerchantStatus;

import java.util.UUID;

public record MerchantResponse (
    UUID id,
    String name,
    String email,
    String businessName,
    BusinessType businessType,
    MerchantStatus merchantStatus
){
}
