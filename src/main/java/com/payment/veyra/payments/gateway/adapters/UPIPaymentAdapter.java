package com.payment.veyra.payments.gateway.adapters;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.gateway.PaymentAdapter;
import com.payment.veyra.payments.gateway.dto.PaymentRequest;
import com.payment.veyra.payments.gateway.dto.PaymentResult;
import com.payment.veyra.payments.processor.PaymentProcessorRouter;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UPIPaymentAdapter implements PaymentAdapter {
    private final PaymentProcessorRouter paymentProcessorRouter;

    @Override
    public PaymentResult initiate(PaymentRequest request){
        log.info("Initiate Payment with UPI Adapter, payment {}", request.PaymentId());

        try {
            PaymentProcessorRequest paymentProcessorRequest = PaymentProcessorRequest.nonCard(
                    request.PaymentId(),
                    PaymentMethod.UPI,
                    request.amount(),
                    request.methodDetails()
            );

            PaymentProcessorResponse response =
                    paymentProcessorRouter.charge(paymentProcessorRequest);

            return switch (response) {
                case PaymentProcessorResponse.Failure failure ->
                        new PaymentResult.Failure(failure.errorCode(), failure.errorDescription());
                case PaymentProcessorResponse.Pending pending ->
                        new PaymentResult.Pending(pending.processorReference());
                case PaymentProcessorResponse.Success success -> new PaymentResult.Success(success.bankReference());
            };
        }catch(Exception e){
            log.warn("UPI Payment failed: {}", request.PaymentId());
            return new PaymentResult.Failure("UPI_FAILURE", e.getMessage());
        }
    }

    @Override
    public PaymentResult capture(UUID paymentId) {
        return  new PaymentResult.Success("UPI_REF");
    }
}
