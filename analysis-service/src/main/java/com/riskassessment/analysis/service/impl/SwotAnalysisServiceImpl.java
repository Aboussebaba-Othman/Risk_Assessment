package com.riskassessment.analysis.service.impl;

import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.enums.AnalysisStatus;
import com.riskassessment.analysis.enums.AnalysisType;
import com.riskassessment.analysis.exception.AnalysisNotFoundException;
import com.riskassessment.analysis.gateway.CompanyGateway;
import com.riskassessment.analysis.gateway.FinancialGateway;
import com.riskassessment.analysis.repository.FinancialAnalysisRepository;
import com.riskassessment.analysis.service.FinancialAnalysisService;
import com.riskassessment.analysis.service.SwotAnalysisService;
import com.riskassessment.analysis.service.SwotCalculationService;
import com.riskassessment.analysis.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwotAnalysisServiceImpl implements SwotAnalysisService {

    private final CompanyGateway companyGateway;
    private final FinancialGateway financialGateway;
    private final FinancialAnalysisService financialAnalysisService;
    private final SwotCalculationService swotCalculationService;
    private final FinancialAnalysisRepository repository;

    @Override
    public FinancialAnalysis getLatestAnalysis(Long companyId) {
        return repository.findTopByCompanyIdOrderByCreatedAtDesc(companyId)
                .orElseThrow(() -> new AnalysisNotFoundException("No SWOT analysis found for company ID " + companyId));
    }

    @Override
    public List<FinancialAnalysis> getAnalysisHistory(Long companyId) {
        return repository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    @Override
    @Transactional
    public FinancialAnalysis performSwotAnalysis(Long companyId) {
        return performSwotAnalysis(companyId, null, null);
    }

    @Override
    @Transactional
    public FinancialAnalysis performSwotAnalysis(Long companyId, BigDecimal scoreValue, String riskLevel) {
        log.info("Starting SWOT orchestration for companyId={}", companyId);
        
        CompanyDTO company = companyGateway.getCompanyById(companyId);
        FinancialDataDTO fin = financialGateway.getLatestFinancials(companyId);

        FinancialAnalysis analysis = new FinancialAnalysis();
        analysis.setCompanyId(companyId);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        analysis.setTenantId(currentUserId != null ? currentUserId : 1L);
        analysis.setAnalysisType(AnalysisType.SWOT);
        analysis.setPeriodStart(LocalDate.now().minusMonths(12));
        analysis.setPeriodEnd(LocalDate.now());
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setOverallHealth(financialAnalysisService.deriveHealth(fin, scoreValue));
        
        financialAnalysisService.populateFinancialSnapshot(analysis, fin);

        String scoreNote = scoreValue != null ? String.format(" | Risk Score: %.0f/100 (%s)", scoreValue.doubleValue(), riskLevel) : "";
        analysis.setNotes("Automated SWOT — " + LocalDate.now() + scoreNote);

        List<AnalysisResult> results = swotCalculationService.generateSwotResults(analysis, company, fin, scoreValue);
        analysis.setResults(results);

        FinancialAnalysis saved = repository.save(analysis);
        log.info("SWOT analysis saved id={} companyId={}", saved.getId(), companyId);
        return saved;
    }
}
