package com.payment.veyra.payments.config;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.processor.PaymentProcessor;
import com.payment.veyra.payments.processor.strategy.CardPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.NetBankingPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.UPIPaymentProcessor;
import com.payment.veyra.payments.processor.strategy.WalletPaymentProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentProcessorConfig {
    @Bean
    public Map<PaymentMethod, PaymentProcessor> paymentProcessorAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentProcessor(),
                PaymentMethod.NETBANKING, new NetBankingPaymentProcessor(),
                PaymentMethod.UPI, new UPIPaymentProcessor(),
                PaymentMethod.WALLET, new WalletPaymentProcessor()
        );
    }
}
