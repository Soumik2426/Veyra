package com.payment.veyra.payments.gateway.adapters;

import com.payment.veyra.payments.gateway.PaymentAdapter;
import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import com.payment.veyra.vault.service.CardTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardPaymentAdapter implements PaymentAdapter {
    private final CardTokenService cardTokenService;

    @Override
    public PaymentResult initiate(PaymentRequest request){
        String token=(String) request.methodDetails().get("token");
        PaymentProcessorResponse response=cardTokenService.charge(
                request.PaymentId(), token, request.amount(), request.method(), request.methodDetails()
        );
        return switch(response){
            case PaymentProcessorResponse.Success success-> new PaymentResult.Success(success.bankReference());
            case PaymentProcessorResponse.Failure failure-> new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
            case PaymentProcessorResponse.Pending pending-> new PaymentResult.Pending(pending.processorReference());
        };
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return new PaymentResult.Success("CARD_REF");
    }
}
