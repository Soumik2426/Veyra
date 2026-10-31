package com.payment.veyra.common.audit;

import com.payment.veyra.merchant.security.MerchantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component("AuditorServiceImpl")
public class AuditorServiceImpl implements AuditorAware<String> {
    private final MerchantContext merchantContext;

    @Override
    public Optional<String> getCurrentAuditor() {
        try {
            String keyId = merchantContext.getKeyId();
            if (keyId != null && !keyId.isBlank()) {
                return Optional.of(keyId);
            }

            if (merchantContext.getMerchantId() != null) {
                return Optional.of(merchantContext.getMerchantId().toString());
            }
        } catch (Exception ignored) {

        }

        return Optional.of("SYSTEM");

    }
}
