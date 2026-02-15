package com.riskassessment.report.service;

import com.riskassessment.report.client.AnalysisClient;
import com.riskassessment.report.client.CompanyClient;
import com.riskassessment.report.client.ScoringClient;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.dto.ScoreDTO;
import com.riskassessment.report.entity.Report;
import com.riskassessment.report.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportGenerationService {

    private final CompanyClient companyClient;
    private final ScoringClient scoringClient;
    private final AnalysisClient analysisClient;
    private final PdfGeneratorService pdfGeneratorService;
    private final ReportRepository reportRepository;

    public byte[] generateCompanyReport(Long companyId) {
        log.info("Starting report generation for companyId: {}", companyId);

        // 1. Fetch Aggregated Data with Fail-Safe
        CompanyDTO company = fetchCompanyData(companyId);
        ScoreDTO score = fetchScoreData(companyId);
        AnalysisDTO analysis = fetchAnalysisData(companyId);

        // 2. Generate PDF
        byte[] pdfContent;
        try {
            pdfContent = pdfGeneratorService.generateRiskReport(company, score, analysis);
        } catch (Exception e) {
            log.error("Error during PDF formatting", e);
            throw new com.riskassessment.report.exception.ReportGenerationException("Failed to generate PDF document",
                    e);
        }

        // 3. Save Metadata
        saveReportMetadata(companyId);

        return pdfContent;
    }

    private CompanyDTO fetchCompanyData(Long companyId) {
        try {
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.error("Failed to fetch company data for id: {}", companyId, e);
            return null; // Return null to indicate missing data in report
        }
    }

    private ScoreDTO fetchScoreData(Long companyId) {
        try {
            return scoringClient.getLatestScore(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch score data for companyId: {}", companyId, e);
            return null;
        }
    }

    private AnalysisDTO fetchAnalysisData(Long companyId) {
        try {
            return analysisClient.performSwotAnalysis(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch analysis data for companyId: {}", companyId, e);
            return null;
        }
    }

    private void saveReportMetadata(Long companyId) {
        try {
            Report report = new Report();
            report.setCompanyId(companyId);
            report.setReportDate(LocalDateTime.now());
            report.setReportType("FULL_RISK_ASSESSMENT");
            report.setFormat("PDF");
            report.setStatus("GENERATED");
            report.setGeneratedBy(1L); // Placeholder for actual user ID
            reportRepository.save(report);
        } catch (Exception e) {
            log.error("Failed to save report metadata for companyId: {}", companyId, e);
            
        }
    }
}
