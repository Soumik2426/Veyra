package com.payment.veyra.payments.gateway.dto;

public sealed interface PaymentResult permits
        PaymentResult.Pending,
        PaymentResult.Success,
        PaymentResult.Failure{

    record Pending(String registrationReference) implements PaymentResult{}
    record Failure(String errorCode, String errorDescription) implements PaymentResult{}
    record Success(String bankReference) implements PaymentResult{}
}
