package com.payment.veyra.payments.simulator;

import com.payment.veyra.common.enums.ChaosMode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "payment.simulator")
public class SimulatorConfig {
    private Integer pollIntervalMs=2000;
    private ChaosMode chaosMode=ChaosMode.NORMAL;

    private Map<String, MethodSimulatorConfig> methods=new HashMap<>();


    public static class MethodSimulatorConfig{
        private Integer minDelaySeconds=1;
        private Integer maxDelaySeconds=5;
        private Integer successRate=80;
    }
}
