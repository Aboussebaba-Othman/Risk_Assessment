package com.riskassessment.scoringservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.riskassessment.scoring.entity")
public class ScoringServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ScoringServiceApplication.class, args);
	}

}
