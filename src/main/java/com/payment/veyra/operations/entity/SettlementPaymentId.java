package com.payment.veyra.operations.entity;

import com.payment.veyra.common.entity.BaseEntity;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SettlementPaymentId extends BaseEntity {
    private UUID settlementId;
    private UUID paymentId;
}
