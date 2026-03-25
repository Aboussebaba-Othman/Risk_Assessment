package com.riskassessment.authservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@ActiveProfiles("test")
class AuthServiceApplicationTests {

	@MockBean
	private JwtDecoder jwtDecoder;

	@Test
	void contextLoads() {
	}

}
