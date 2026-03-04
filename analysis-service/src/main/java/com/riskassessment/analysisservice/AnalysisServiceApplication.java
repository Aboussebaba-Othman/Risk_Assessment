package com.riskassessment.analysisservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.riskassessment.analysis.entity")
@EnableJpaRepositories("com.riskassessment.analysis.repository")
@EnableFeignClients(basePackages = "com.riskassessment.analysis.client")
@ComponentScan(basePackages = {
		"com.riskassessment.analysisservice",
		"com.riskassessment.analysis"
})
public class AnalysisServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AnalysisServiceApplication.class, args);
	}

}
