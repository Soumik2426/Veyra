package com.payment.veyra.vault.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Year;

public class CheckYear implements ConstraintValidator<ExpiryYear, Integer> {
    @Override
    public boolean isValid(Integer input, ConstraintValidatorContext context) {
        int currYear = Year.now().getValue();

        if (input == null) {
            return false;
        }

        return input>=currYear;
    }
}
