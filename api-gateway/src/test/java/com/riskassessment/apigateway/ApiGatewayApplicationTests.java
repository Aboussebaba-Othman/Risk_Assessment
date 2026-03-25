package com.riskassessment.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@ActiveProfiles("test")
class ApiGatewayApplicationTests {

	@MockBean
	private ReactiveJwtDecoder reactiveJwtDecoder;

	@Test
	void contextLoads() {
	}

}
