package com.payment.veyra.merchant.entity;

import com.payment.veyra.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant_webhook_config",
        indexes = {
            @Index(name="idx_merchant_webhook_config_merchant_id", columnList = "merchant_id, enabled")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MerchantWebhookConfig extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(nullable = false, length = 500)
    private String targetUrl;

    @Column(length = 250)
    private String webhookSecretHash;

    @Column(nullable = false)
    private Boolean enabled=true;

    @Column(length = 250)
    private String eventType;
}
