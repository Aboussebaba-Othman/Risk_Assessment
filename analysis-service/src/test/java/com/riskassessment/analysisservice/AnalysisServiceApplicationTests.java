package com.riskassessment.analysisservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import com.riskassessment.analysis.AnalysisServiceApplication;
import org.springframework.test.context.ActiveProfiles;


@SpringBootTest(classes = AnalysisServiceApplication.class)
@ActiveProfiles("test")
class AnalysisServiceApplicationTests {

	@Test
	void contextLoads() {
	}

}
