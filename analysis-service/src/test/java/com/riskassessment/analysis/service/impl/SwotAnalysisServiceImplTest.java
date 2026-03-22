package com.riskassessment.analysis.service.impl;

import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.exception.AnalysisNotFoundException;
import com.riskassessment.analysis.gateway.CompanyGateway;
import com.riskassessment.analysis.gateway.FinancialGateway;
import com.riskassessment.analysis.repository.FinancialAnalysisRepository;
import com.riskassessment.analysis.security.SecurityUtils;
import com.riskassessment.analysis.service.FinancialAnalysisService;
import com.riskassessment.analysis.service.SwotCalculationService;
import com.riskassessment.analysis.enums.OverallHealth;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SwotAnalysisServiceImplTest {

    @Mock
    private CompanyGateway companyGateway;

    @Mock
    private FinancialGateway financialGateway;

    @Mock
    private FinancialAnalysisService financialAnalysisService;

    @Mock
    private SwotCalculationService swotCalculationService;

    @Mock
    private FinancialAnalysisRepository repository;

    @InjectMocks
    private SwotAnalysisServiceImpl swotAnalysisService;

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
    void getLatestAnalysis_Success() {
        FinancialAnalysis analysis = new FinancialAnalysis();
        analysis.setId(10L);
        when(repository.findTopByCompanyIdOrderByCreatedAtDesc(99L)).thenReturn(Optional.of(analysis));

        FinancialAnalysis result = swotAnalysisService.getLatestAnalysis(99L);
        assertNotNull(result);
        assertEquals(10L, result.getId());
    }

    @Test
    void getLatestAnalysis_NotFound_ThrowsException() {
        when(repository.findTopByCompanyIdOrderByCreatedAtDesc(99L)).thenReturn(Optional.empty());

        assertThrows(AnalysisNotFoundException.class, () -> swotAnalysisService.getLatestAnalysis(99L));
    }

    @Test
    void performSwotAnalysis_FullRun_Success() {
        Long companyId = 99L;
        securityUtilsMock.when(SecurityUtils::getCurrentUserId).thenReturn(88L);
        
        CompanyDTO mockCompany = new CompanyDTO();
        FinancialDataDTO mockFin = new FinancialDataDTO();
        
        when(companyGateway.getCompanyById(companyId)).thenReturn(mockCompany);
        when(financialGateway.getLatestFinancials(companyId)).thenReturn(mockFin);
        
        when(financialAnalysisService.deriveHealth(eq(mockFin), any())).thenReturn(OverallHealth.EXCELLENT);
        
        AnalysisResult mockRes = new AnalysisResult();
        mockRes.setDescription("Test Strength");
        when(swotCalculationService.generateSwotResults(any(), eq(mockCompany), eq(mockFin), any()))
            .thenReturn(Collections.singletonList(mockRes));
            
        FinancialAnalysis savedAnalysis = new FinancialAnalysis();
        savedAnalysis.setId(5L);
        savedAnalysis.setTenantId(88L);
        
        when(repository.save(any(FinancialAnalysis.class))).thenReturn(savedAnalysis);

        FinancialAnalysis result = swotAnalysisService.performSwotAnalysis(companyId, BigDecimal.valueOf(85.5), "LOW");

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(88L, result.getTenantId());
        
        verify(financialAnalysisService).populateFinancialSnapshot(any(), eq(mockFin));
        verify(repository).save(any(FinancialAnalysis.class));
    }
}
