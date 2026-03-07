package com.riskassessment.scoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.riskassessment.scoringservice",
		"com.riskassessment.scoring"
})
@EntityScan("com.riskassessment.scoring.entity")
@EnableJpaRepositories("com.riskassessment.scoring.repository")
@EnableFeignClients(basePackages = "com.riskassessment.scoring.client")
public class ScoringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoringServiceApplication.class, args);
	}

}
