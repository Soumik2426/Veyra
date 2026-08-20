package com.payment.veyra.common.exceptions;

import lombok.Getter;

@Getter
public class ResourceNotFoundException extends RuntimeException{
    private final String resourceName;
    private final String identifier;

    public ResourceNotFoundException(String resourceName, String identifier) {
        super(resourceName +" not found: "+ identifier);
        this.resourceName = resourceName;
        this.identifier = identifier;
    }
}
