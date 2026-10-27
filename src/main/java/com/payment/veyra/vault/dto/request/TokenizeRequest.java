package com.payment.veyra.vault.dto.request;

import com.payment.veyra.vault.validation.ExpiryYear;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.LuhnCheck;

import java.util.UUID;

public record TokenizeRequest(
        @NotBlank(message = "PAN is required")
        @LuhnCheck(message = "Invalid PAN")
        @Pattern(regexp = "^[0-9]{13,19}$", message = "PAN must be between 13 and 19 digits")
        String pan,

        @NotBlank(message = "CVV is required")
        @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV length is invalid")
        String cvv,

        @NotNull(message = "Expiry month is required")
        @Min(value = 1, message = "Expiry month must be between 1 and 12")
        @Max(value = 12, message = "Expiry month must be between 1 and 12")
        Integer expiryMonth,

        @NotNull(message = "Expiry year is required")
        @ExpiryYear
        Integer expiryYear,

        UUID customerId,

        @Size(min = 3, message = "Cardholder name must be at least 3 characters long")
        String cardholderName
) {
}
