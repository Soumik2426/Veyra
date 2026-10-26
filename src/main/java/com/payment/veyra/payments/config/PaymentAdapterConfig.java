package com.payment.veyra.payments.config;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.gateway.PaymentAdapter;
import com.payment.veyra.payments.gateway.adapters.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class PaymentAdapterConfig {
    private final UPIPaymentAdapter upiPaymentAdapter;
    private final NetBankingPaymentAdapter netBankingPaymentAdapter;
    private final CardPaymentAdapter cardPaymentAdapter;
    private final WalletPaymentAdapter walletPaymentAdapter;

    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, cardPaymentAdapter,
                PaymentMethod.NETBANKING, netBankingPaymentAdapter,
                PaymentMethod.UPI, upiPaymentAdapter,
                PaymentMethod.WALLET, walletPaymentAdapter
        );
    }
}
