package com.payment.veyra.payments.simulator;

import com.payment.veyra.common.enums.ChaosMode;
import com.payment.veyra.common.enums.PaymentStatus;
import com.payment.veyra.common.util.RandomizerUtil;
import com.payment.veyra.payments.entity.Payment;
import com.payment.veyra.payments.repository.PaymentRepository;
import com.payment.veyra.payments.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class BankCallbackSimulator {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final SimulatorConfig simulatorConfig;

    @Scheduled(fixedDelayString = "${payment.simulator.poll-interval-ms:5000}")
    public void processCallbacks(){
        LocalDateTime globalWindow = LocalDateTime.now().minusSeconds(1);
        List<Payment> candidates= paymentRepository.findByStatusAndCreatedAtBefore(PaymentStatus.AUTHORIZING, globalWindow);

        log.info("Simulating payments for {} payments", candidates.size());

        if(candidates.isEmpty()){
            return;
        }

        for(Payment payment:candidates){
            simulateCallback(payment);
        }
    }

    private void simulateCallback(Payment payment){
        SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig=simulatorConfig.configFor(payment.getPaymentMethod());
        LocalDateTime dueAt=dueAt(payment, methodSimulatorConfig);

        if(LocalDateTime.now().isBefore(dueAt)){
            return;
        }

        ChaosMode chaosMode=simulatorConfig.getChaosMode();

        switch (chaosMode){
            case SUCCESS -> resolve(payment, true);
            case FAILURE -> resolve(payment, false);
            case TIMEOUT ->{
                log.info("BankCallback Simulator: Payment timed out");
            }

            case NORMAL -> resolve(payment, successRate(payment, methodSimulatorConfig));
        }
    }


    private void resolve(Payment payment, boolean approve){
        if(approve){
            String bankRef="SIM_BANK_REF"+ RandomizerUtil.randomBase64(8);
            paymentService.resolveAuthorization(payment.getId(), true, bankRef, null, null);
        }else{
            paymentService.resolveAuthorization(payment.getId(), false, null, "SIM_BANK_ERROR_CODE", "Simulated Bank Decline");
        }
    }

    private boolean successRate(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int successRate=methodSimulatorConfig.getSuccessRate();
        int bucket=Math.abs(payment.getId().hashCode())%100;
        return bucket<successRate;
    }


    private LocalDateTime dueAt(Payment payment, SimulatorConfig.MethodSimulatorConfig methodSimulatorConfig){
        int range = methodSimulatorConfig.getMaxDelaySeconds() - methodSimulatorConfig.getMinDelaySeconds();
        int delaySeconds = methodSimulatorConfig.getMinDelaySeconds() + Math.abs(payment.getId().hashCode()) % (range + 1);

        if(simulatorConfig.getChaosMode()== ChaosMode.SLOW){
            delaySeconds*=2;
        }

        return payment.getCreatedAt().plusSeconds(delaySeconds);
    }
}
