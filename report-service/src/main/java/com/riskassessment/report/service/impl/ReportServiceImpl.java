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
import com.riskassessment.report.service.IReportService;
import com.riskassessment.report.service.PdfGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements IReportService {

    private final CompanyGateway companyGateway;
    private final ScoringGateway scoringGateway;
    private final AnalysisGateway analysisGateway;
    private final PdfGeneratorService pdfGeneratorService;
    private final ReportRepository reportRepository;

    @Override
    public byte[] generateCompanyReport(Long companyId) {
        log.info("Starting report generation for companyId: {}", companyId);

        CompanyDTO company     = companyGateway.getCompanyById(companyId);
        ScoreDTO score         = scoringGateway.getLatestScore(companyId);
        RecommendationDTO rec  = scoringGateway.getRecommendation(companyId);
        AnalysisDTO analysis   = analysisGateway.getSwotAnalysis(companyId);

        byte[] pdfContent;
        try {
            pdfContent = pdfGeneratorService.generateRiskReport(company, score, rec, analysis);
        } catch (Exception e) {
            log.error("Error during PDF generation for companyId={}: {}", companyId, e.getMessage(), e);
            throw new ReportGenerationException("Failed to generate PDF document", e);
        }

        saveReportMetadata(companyId);
        return pdfContent;
    }

    private void saveReportMetadata(Long companyId) {
        try {
            Report report = new Report();
            report.setCompanyId(companyId);
            report.setReportDate(LocalDateTime.now());
            report.setFormat("PDF");
            report.setStatus("GENERATED");
            Long userId = SecurityUtils.getCurrentUserId();
            if (userId == null) {
                log.warn("Report generated without an authenticated numeric user ID. Defaulting to SYSTEM (1L)");
                userId = 1L;
            }
            report.setGeneratedBy(userId);
            reportRepository.save(report);
        } catch (Exception e) {
            log.error("Failed to persist report metadata for companyId={}: {}", companyId, e.getMessage());
        }
    }
}
