package com.riskassessment.analysis.controller;

import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.enums.AnalysisType;
import com.riskassessment.analysis.service.SwotAnalysisService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AnalysisControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Since trigger relies on Feign Clients (Gateways), we mock the Service layer for this IT
    private SwotAnalysisService swotAnalysisService;

    @Test
    void triggerSwot_Authorized_Returns200() throws Exception {
        Long companyId = 99L;
        FinancialAnalysis mockedAnalysis = new FinancialAnalysis();
        mockedAnalysis.setId(5L);
        mockedAnalysis.setCompanyId(companyId);
        mockedAnalysis.setAnalysisType(AnalysisType.SWOT);

        when(swotAnalysisService.performSwotAnalysis(companyId)).thenReturn(mockedAnalysis);

        mockMvc.perform(post("/analysis/companies/{companyId}/trigger", companyId)
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "88"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(5)))
                .andExpect(jsonPath("$.analysisType", is("SWOT")));
    }

    @Test
    void getLatest_Authorized_ReturnsLatest() throws Exception {
        Long companyId = 99L;
        FinancialAnalysis mockedAnalysis = new FinancialAnalysis();
        mockedAnalysis.setId(10L);
        mockedAnalysis.setCompanyId(companyId);

        when(swotAnalysisService.getLatestAnalysis(companyId)).thenReturn(mockedAnalysis);

        mockMvc.perform(get("/analysis/companies/{companyId}/latest", companyId)
                        .with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10)));
    }
}
