package com.riskassessment.scoring.controller;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.service.IScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.riskassessment.scoring.enums.RiskLevel;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ScoringControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean 
    private IScoringService scoringService;

    @Test
    void calculateScore_Authorized_Returns200() throws Exception {
        Long companyId = 99L;
        Score mockedScore = new Score();
        mockedScore.setId(5L);
        mockedScore.setCompanyId(companyId);
        mockedScore.setOverallScore(BigDecimal.valueOf(85));
        mockedScore.setRiskLevel(RiskLevel.LOW_RISK);

        when(scoringService.calculateScore(eq(companyId), eq(123L))).thenReturn(mockedScore);

        mockMvc.perform(post("/api/v1/scores/calculate/{companyId}", companyId)
                        .with(jwt().jwt(jwt -> jwt.claim("tenant_id", "123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.riskLevel", is("LOW_RISK")));
    }

    @Test
    void getLatestScore_Authorized_ReturnsLatest() throws Exception {
        Long companyId = 99L;
        Score mockedScore = new Score();
        mockedScore.setId(10L);
        mockedScore.setOverallScore(BigDecimal.valueOf(90));

        when(scoringService.getLatestScore(eq(companyId), eq(123L))).thenReturn(mockedScore);

        mockMvc.perform(get("/api/v1/scores/companies/{companyId}/latest", companyId)
                        .with(jwt().jwt(jwt -> jwt.claim("tenant_id", "123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }
    
    @Test
    void getRecommendation_Authorized_ReturnsRecommendation() throws Exception {
        Long companyId = 99L;
        RecommendationDTO rec = new RecommendationDTO();
        rec.setDecision("Approve");
        
        when(scoringService.getRecommendation(eq(companyId), eq(123L))).thenReturn(rec);
        
        mockMvc.perform(get("/api/v1/scores/companies/{companyId}/recommendation", companyId)
                        .with(jwt().jwt(jwt -> jwt.claim("tenant_id", "123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.decision", is("Approve")));
    }
}
