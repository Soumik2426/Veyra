package com.payment.veyra.payments.repository;

import com.payment.veyra.common.enums.PaymentStatus;
import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import com.payment.veyra.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_Id(OrderRecord order);

    Optional<Payment> findByIdAndMerchantId(UUID paymentId, UUID merchantId);

    List<Payment> findByStatusAndCreatedAtBefore(PaymentStatus paymentStatus, LocalDateTime globalWindow);
}