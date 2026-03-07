package com.riskassessment.reportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.riskassessment.reportservice",
		"com.riskassessment.report"
})
@EnableFeignClients(basePackages = "com.riskassessment.report.client")
@EnableMongoRepositories("com.riskassessment.report.repository")
public class ReportServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReportServiceApplication.class, args);
	}

}
