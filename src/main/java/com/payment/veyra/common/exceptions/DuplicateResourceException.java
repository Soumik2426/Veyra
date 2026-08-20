package com.payment.veyra.common.exceptions;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException{
    private final String errorCode;
    private final String message;

    public DuplicateResourceException(String errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }
}
