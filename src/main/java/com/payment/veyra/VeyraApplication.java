package com.payment.veyra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "AuditorServiceImpl")
@EnableScheduling
public class VeyraApplication {

	public static void main(String[] args) {
		SpringApplication.run(VeyraApplication.class, args);
	}

}
