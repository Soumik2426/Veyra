package com.payment.veyra.payments.statemachine;

import com.payment.veyra.common.enums.PaymentActor;
import com.payment.veyra.common.enums.PaymentEvent;
import com.payment.veyra.common.enums.PaymentStatus;
import com.payment.veyra.payments.entity.Payment;
import com.payment.veyra.payments.entity.PaymentTransitionLog;
import com.payment.veyra.payments.repository.PaymentTransitionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentTransitionService {
    private final PaymentTransitionLogRepository paymentTransitionLogRepository;
    private final PaymentStateMachine paymentStateMachine;

    public PaymentStatus apply(Payment payment, PaymentEvent event){
        PaymentStatus next=paymentStateMachine.transition(payment.getStatus(), event);
        payment.setStatus(next);
        PaymentTransitionLog log=PaymentTransitionLog.builder()
                .payment(payment)
                .fromStatus(payment.getStatus())
                .event(event)
                .toStatus(next)
                .actor(PaymentActor.SYSTEM) //TODO: Fetch merchantId from context and set actor accordingly
                .occuredAt(LocalDateTime.now())
                .build();

        paymentTransitionLogRepository.save(log);
        return next;
    }
}
