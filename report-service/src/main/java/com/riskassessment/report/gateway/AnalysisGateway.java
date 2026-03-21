package com.riskassessment.report.gateway;

import com.riskassessment.report.client.AnalysisClient;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisGateway {

    private final AnalysisClient analysisClient;

    public AnalysisDTO getSwotAnalysis(Long companyId) {
        try {
            return analysisClient.performSwotAnalysis(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch SWOT analysis for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve SWOT analysis from analysis-service", e);
        }
    }
}
