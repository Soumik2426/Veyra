package com.payment.veyra.merchant.dto.request;

import com.payment.veyra.common.enums.Environment;

public record CreateApiKeyRequest(
        Environment environment
) {
}
