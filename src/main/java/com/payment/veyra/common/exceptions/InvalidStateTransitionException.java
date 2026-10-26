package com.payment.veyra.common.exceptions;

import lombok.Getter;

@Getter
public class InvalidStateTransitionException extends RuntimeException{
    private final String fromState;
    private final String toState;

    public InvalidStateTransitionException(String fromState, String toState){
        super("Invalid state transition from " + fromState + " to " + toState);
        this.fromState = fromState;
        this.toState = toState;
    }

}
