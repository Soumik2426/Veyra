package com.payment.veyra.payments.repository;

import com.payment.veyra.payments.dto.response.PaymentResponse;
import com.payment.veyra.payments.entity.OrderRecord;
import com.payment.veyra.payments.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByOrder_Id(OrderRecord order);
}