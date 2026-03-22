package com.riskassessment.report.service.impl;

import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import com.riskassessment.report.entity.Report;
import com.riskassessment.report.exception.ReportGenerationException;
import com.riskassessment.report.gateway.AnalysisGateway;
import com.riskassessment.report.gateway.CompanyGateway;
import com.riskassessment.report.gateway.ScoringGateway;
import com.riskassessment.report.repository.ReportRepository;
import com.riskassessment.report.security.SecurityUtils;
import com.riskassessment.report.service.PdfGeneratorService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {

    @Mock
    private CompanyGateway companyGateway;

    @Mock
    private ScoringGateway scoringGateway;

    @Mock
    private AnalysisGateway analysisGateway;

    @Mock
    private PdfGeneratorService pdfGeneratorService;

    @Mock
    private ReportRepository reportRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    private MockedStatic<SecurityUtils> securityUtilsMock;

    @BeforeEach
    void setUp() {
        securityUtilsMock = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        securityUtilsMock.close();
    }

    @Test
    void generateCompanyReport_Success() throws Exception {
        Long companyId = 99L;
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(88L);
        
        CompanyDTO mockCompany = new CompanyDTO();
        ScoreDTO mockScore = new ScoreDTO();
        RecommendationDTO mockRec = new RecommendationDTO();
        AnalysisDTO mockAnalysis = new AnalysisDTO();
        
        when(companyGateway.getCompanyById(companyId)).thenReturn(mockCompany);
        when(scoringGateway.getLatestScore(companyId)).thenReturn(mockScore);
        when(scoringGateway.getRecommendation(companyId)).thenReturn(mockRec);
        when(analysisGateway.getSwotAnalysis(companyId)).thenReturn(mockAnalysis);
        
        byte[] expectedPdf = new byte[]{1, 2, 3};
        when(pdfGeneratorService.generateRiskReport(mockCompany, mockScore, mockRec, mockAnalysis))
            .thenReturn(expectedPdf);

        byte[] result = reportService.generateCompanyReport(companyId);

        assertNotNull(result);
        assertArrayEquals(expectedPdf, result);
        
        verify(reportRepository).save(any(Report.class));
    }

    @Test
    void generateCompanyReport_PdfGenerationFails_ThrowsException() throws Exception {
        Long companyId = 99L;
        
        when(companyGateway.getCompanyById(companyId)).thenReturn(new CompanyDTO());
        when(scoringGateway.getLatestScore(companyId)).thenReturn(new ScoreDTO());
        when(scoringGateway.getRecommendation(companyId)).thenReturn(new RecommendationDTO());
        when(analysisGateway.getSwotAnalysis(companyId)).thenReturn(new AnalysisDTO());
        
        when(pdfGeneratorService.generateRiskReport(any(), any(), any(), any()))
            .thenThrow(new RuntimeException("PDF engine failed"));

        assertThrows(ReportGenerationException.class, () -> reportService.generateCompanyReport(companyId));
        verify(reportRepository, never()).save(any(Report.class));
    }
}
