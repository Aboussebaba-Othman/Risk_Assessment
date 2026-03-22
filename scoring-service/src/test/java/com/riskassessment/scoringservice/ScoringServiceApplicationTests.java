package com.riskassessment.scoringservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"eureka.client.enabled=false",
		"spring.cloud.config.enabled=false",
		"spring.liquibase.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:testdb;NON_KEYWORDS=VALUE",
		"spring.datasource.driverClassName=org.h2.Driver",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})
class ScoringServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
