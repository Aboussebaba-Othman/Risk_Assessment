package com.riskassessment.alertservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@ActiveProfiles("test")
class AlertServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
