package com.payment.veyra.payments.config;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.strategy.CardPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.NetBankingPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.UPIPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.WalletPaymentProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentProcessorConfig {

    private final CardPaymentProcessor cardPaymentProcessor;
    private final NetBankingPaymentProcessor netBankingPaymentProcessor;
    private final UPIPaymentProcessor upiPaymentProcessor;
    private final WalletPaymentProcessor walletPaymentProcessor;

    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, cardPaymentProcessor,
                PaymentMethod.NETBANKING, netBankingPaymentProcessor,
                PaymentMethod.UPI, upiPaymentProcessor,
                PaymentMethod.WALLET, walletPaymentProcessor
        );
    }
}
