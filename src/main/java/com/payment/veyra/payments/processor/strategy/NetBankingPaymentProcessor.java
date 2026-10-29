package com.payment.veyra.payments.processor.strategy;

import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NetBankingPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String BANK_CODE_FAIL="BANK_CODE_FAIL";

        String bankCode=request.methodDetails()!=null ?
                request.methodDetails().get("BANK").toString():null;

        if(BANK_CODE_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure(
                    "Bank Rejected",
                    "Bank Rejected the transaction"
            );
        }

        String processorReference="NBK_PROCESSOR_"+ RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Pending(
                processorReference
        );
    }
}
