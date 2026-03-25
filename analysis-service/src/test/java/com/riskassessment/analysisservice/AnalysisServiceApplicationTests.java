package com.riskassessment.analysisservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.riskassessment.analysis.AnalysisServiceApplication;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = AnalysisServiceApplication.class, properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@ActiveProfiles("test")
class AnalysisServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
