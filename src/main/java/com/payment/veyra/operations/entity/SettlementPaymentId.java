package com.payment.veyra.operations.entity;

import com.payment.veyra.common.entity.BaseEntity;
import jakarta.persistence.Embeddable;

import java.util.UUID;

@Embeddable
public class SettlementPaymentId {
    private UUID settlementId;
    private UUID paymentId;
}
