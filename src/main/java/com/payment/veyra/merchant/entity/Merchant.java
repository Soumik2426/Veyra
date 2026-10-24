package com.payment.veyra.merchant.entity;

import com.payment.veyra.common.entity.BaseEntity;
import com.payment.veyra.common.enums.BusinessType;
import com.payment.veyra.common.enums.MerchantStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "merchant",
        indexes = {
            @Index(name = "idx_merchant_email", columnList = "email"),
            @Index(name = "idx_merchant_status", columnList = "status"),
            @Index(name = "idx_merchant_contactNumber", columnList = "contactNumber")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Merchant extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 200, unique = true)
    private String email;

    @Column(length = 20)
    private String contactNumber;

    @Column(length = 50)
    @Enumerated(EnumType.STRING)
    private BusinessType businessType;

    @Column(length = 100)
    private String businessName;

    @Column(length = 200)
    private String WebsiteUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 200, nullable = false)
    private MerchantStatus status=MerchantStatus.PENDING_KYC;

    @Column(length = 20)
    private String gstId;

    @Column(length = 20)
    private String panId;

    @Column(length = 100)
    private String settlementBankAccountName;

    @Column(length = 50)
    private String settlementBankAccountNumber;

    @Column(length = 50)
    private String settlementBankIfsc;

    @Column(length = 100)
    private String settlementBankAccountHolderName;
}
