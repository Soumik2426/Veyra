package com.payment.veyra.mapper;

import com.payment.veyra.merchant.dto.response.ApiKeyCreateResponse;
import com.payment.veyra.merchant.dto.response.ApiKeyResponse;
import com.payment.veyra.merchant.entity.ApiKey;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ApiKeyMapper {
    ApiKeyCreateResponse toResponse(ApiKey apiKey);
    ApiKeyResponse toApiKeyResponse(ApiKey apiKey);
}
