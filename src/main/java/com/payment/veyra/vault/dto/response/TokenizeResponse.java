package com.payment.veyra.vault.dto.response;

import com.payment.veyra.common.enums.CardBrand;

public record TokenizeResponse(
        String token,
        String lastFourDigits,
        CardBrand cardName,
        Integer expiryMonth,
        Integer expiryYear
) {
}
