package com.payment.veyra.payments.config;

import com.payment.veyra.common.enums.PaymentMethod;
import com.payment.veyra.payments.gateway.PaymentAdapter;
import com.payment.veyra.payments.gateway.adapters.CardPaymentAdapter;
import com.payment.veyra.payments.gateway.adapters.NetBankingAdapter;
import com.payment.veyra.payments.gateway.adapters.UPIPaymentAdapter;
import com.payment.veyra.payments.gateway.adapters.WalletPaymentAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class PaymentAdapterConfig {
    @Bean
    public Map<PaymentMethod, PaymentAdapter> paymentAdapterMap(){
        return Map.of(
                PaymentMethod.CARD, new CardPaymentAdapter(),
                PaymentMethod.NETBANKING, new NetBankingAdapter(),
                PaymentMethod.UPI, new UPIPaymentAdapter(),
                PaymentMethod.WALLET, new WalletPaymentAdapter()
        );
    }
}
