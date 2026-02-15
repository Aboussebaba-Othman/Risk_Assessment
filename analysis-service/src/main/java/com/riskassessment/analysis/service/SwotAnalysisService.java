package com.riskassessment.analysis.service;

import com.riskassessment.analysis.client.CompanyClient;
import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.entity.enums.AnalysisStatus;
import com.riskassessment.analysis.entity.enums.AnalysisType;
import com.riskassessment.analysis.entity.enums.OverallHealth;
import com.riskassessment.analysis.entity.enums.ResultType;
import com.riskassessment.analysis.entity.enums.Severity;
import com.riskassessment.analysis.logic.SwotGenerator;
import com.riskassessment.analysis.repository.AnalysisResultRepository;
import com.riskassessment.analysis.repository.FinancialAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwotAnalysisService {

    private final AnalysisResultRepository analysisResultRepository;
    private final FinancialAnalysisRepository financialAnalysisRepository;
    private final CompanyClient companyClient;
    private final SwotGenerator swotGenerator;

    @Transactional
    public AnalysisResult performSwotAnalysis(Long companyId) {
        log.info("Starting SWOT analysis for companyId: {}", companyId);

        // 1. Fetch Company Data
        CompanyDTO company;
        try {
            company = companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch company data via feign, using fallback/mock for now", e);
            company = new CompanyDTO();
            company.setId(companyId);
            company.setIndustrySector("Technology"); // Default fallback
        }

        // Mock Financial Data (Need a proper Financial Service or Scoring Client here)
        BigDecimal revenue = new BigDecimal("1200000");
        BigDecimal netResult = new BigDecimal("150000");
        BigDecimal equity = new BigDecimal("600000");
        BigDecimal debt = new BigDecimal("300000");

        // 2. Generate SWOT Logic (Role of SwotGenerator)
        List<String> strengths = swotGenerator.generateStrengths(company, revenue, netResult, equity);
        List<String> weaknesses = swotGenerator.generateWeaknesses(company, revenue, netResult, debt);
        List<String> opportunities = swotGenerator.generateOpportunities(company);
        List<String> threats = swotGenerator.generateThreats(company, netResult);

        // 3. Create Parent Analysis (FinancialAnalysis)
        FinancialAnalysis analysis = new FinancialAnalysis();
        analysis.setCompanyId(companyId);
        analysis.setTenantId(1L); // Default Context
        analysis.setAnalysisType(AnalysisType.CUSTOM); // Using CUSTOM as SWOT specific is not in Enum yet
        analysis.setPeriodStart(LocalDate.now().minusMonths(1));
        analysis.setPeriodEnd(LocalDate.now());
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setOverallHealth(OverallHealth.GOOD); // Simplified logic
        analysis.setAnalyzedBy(1L); // System
        analysis.setAnalyzedAt(LocalDateTime.now());

        // Save parent first
        analysis = financialAnalysisRepository.save(analysis);

        // 4. Create Result Detail (AnalysisResult)
        AnalysisResult result = new AnalysisResult();
        result.setAnalysis(analysis); // Link to parent
        result.setResultType(ResultType.STRENGTH); // Defaulting for the summary object
        result.setCategory("Strategic");
        result.setTitle("SWOT Analysis Summary");
        result.setSeverity(Severity.INFO);

        // Serialize SWOT to String
        StringBuilder summary = new StringBuilder("SWOT Analysis:\n");
        summary.append("Strengths: ").append(String.join(", ", strengths)).append("\n");
        summary.append("Weaknesses: ").append(String.join(", ", weaknesses)).append("\n");
        summary.append("Opportunities: ").append(String.join(", ", opportunities)).append("\n");
        summary.append("Threats: ").append(String.join(", ", threats));

        result.setDescription(summary.toString());
        result.setRecommendation("Proceed with business relationship. Monitor debt levels.");

        return analysisResultRepository.save(result);
    }
}
