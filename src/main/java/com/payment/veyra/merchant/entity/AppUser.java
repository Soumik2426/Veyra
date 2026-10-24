package com.payment.veyra.merchant.entity;

import com.payment.veyra.common.entity.BaseEntity;
import com.payment.veyra.common.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "app_user",
        indexes = {
            @Index(name="idx_app_user_merchant_id", columnList = "merchant_id")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppUser extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "merchant_id", nullable = false)
    private Merchant merchant;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(length = 100, nullable = false)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;
}
