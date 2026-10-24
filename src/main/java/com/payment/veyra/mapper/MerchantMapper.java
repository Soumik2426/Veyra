package com.payment.veyra.mapper;

import com.payment.veyra.merchant.dto.request.MerchantSignupRequest;
import com.payment.veyra.merchant.dto.response.MerchantResponse;
import com.payment.veyra.merchant.entity.Merchant;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MerchantMapper {
    Merchant toMerchantEntity(MerchantSignupRequest request);
    MerchantResponse toResponse(Merchant merchant);
}
