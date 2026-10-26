package com.payment.veyra.payments.processor.strategy;

import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.dto.PaymentProcessorRequest;
import com.payment.veyra.payments.processor.dto.PaymentProcessorResponse;

public class UPIPaymentProcessor implements PaymentProcessor {
    @Override
    public PaymentProcessorResponse charge(PaymentProcessorRequest request) {
        final String VPA_FAIL="fail@okaxis";

        String bankCode=request.methodDetails()!=null ?
                request.methodDetails().get("VPA").toString():null;

        if(VPA_FAIL.equals(bankCode)){
            return new PaymentProcessorResponse.Failure(
                    "UPI Rejected",
                    "Bank Rejected the transaction"
            );
        }

        String processorReference="UPI_PROCESSOR_"+ RandomizerUtil.randomBase64(16);
        String bankRef="BANK_REF_"+RandomizerUtil.randomBase64(16);
        return new PaymentProcessorResponse.Success(
                processorReference,
                bankRef
        );
    }
}
