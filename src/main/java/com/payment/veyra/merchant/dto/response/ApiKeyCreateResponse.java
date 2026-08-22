package com.payment.veyra.merchant.dto.response;

import com.payment.veyra.common.enums.Environment;

import java.util.UUID;

public record ApiKeyCreateResponse(
        UUID id,
        String KeyId,
        String KeySecret,
        Environment environment
) {
}
