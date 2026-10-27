package com.payment.veyra.payments.processor.strategy;

import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CardPaymentProcessor implements PaymentProcessor {

    public static final String PAN_CARD_DECLINED = "4000000000000002";
    public static final String PAN_CARD_EXPIRED = "4000000000000001";

    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        String pan=request.pan();
        if(PAN_CARD_DECLINED.equals(pan)){
            log.warn("Card Declined");
            return new PaymentProcessorResponse.Failure("CARD_DECLINED", "Card declined by bank");
        }
        if(PAN_CARD_EXPIRED.equals(pan)){
            log.warn("Card Expired");
            return new PaymentProcessorResponse.Failure("CARD_EXPIRED", "Card has expired");
        }

        String processorReference="CARD_PROCESSOR_"+ RandomizerUtil.randomBase64(16);

        return new PaymentProcessorResponse.Pending(processorReference);
    }
}
