package com.riskassessment.report.controller;

import com.riskassessment.report.service.IReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReportControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IReportService reportService;

    @Test
    void downloadCompanyReport_Authorized_ReturnsPdf() throws Exception {
        Long companyId = 99L;
        byte[] pdfContent = "Dummy PDF Content".getBytes();

        when(reportService.generateCompanyReport(companyId)).thenReturn(pdfContent);

        mockMvc.perform(get("/api/v1/reports/company/{companyId}/download", companyId)
                        .with(jwt().jwt(jwt -> jwt.claim("scope", "read").claim("authorities", "ROLE_ANALYST"))))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=risk_report_99.pdf"))
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes(pdfContent));
    }

    @Test
    void downloadCompanyReport_ForbiddenIfNoAuthorities_Returns403() throws Exception {
        Long companyId = 99L;
        
        mockMvc.perform(get("/api/v1/reports/company/{companyId}/download", companyId)
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }
}
