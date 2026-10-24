package com.payment.veyra.common.exceptions;

import lombok.Getter;

@Getter
public class BusinessRuleViolationException extends RuntimeException {
    private final String errorCode;
    private final String message;

    public BusinessRuleViolationException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
