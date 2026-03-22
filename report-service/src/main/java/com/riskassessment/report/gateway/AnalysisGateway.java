package com.riskassessment.report.gateway;

import com.riskassessment.report.client.AnalysisClient;
import com.riskassessment.report.dto.AnalysisDTO;
import com.riskassessment.report.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AnalysisGateway {

    private final AnalysisClient analysisClient;

    @CircuitBreaker(name = "analysis-service", fallbackMethod = "fallbackGetSwotAnalysis")
    public AnalysisDTO getSwotAnalysis(Long companyId) {
        try {
            return analysisClient.performSwotAnalysis(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch SWOT analysis for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve SWOT analysis from analysis-service", e);
        }
    }

    public AnalysisDTO fallbackGetSwotAnalysis(Long companyId, Exception ex) {
        log.error("Circuit breaker OPEN for analysis-service [getSwotAnalysis] companyId={}: {}", companyId, ex.getMessage());
        throw new ExternalServiceException("analysis-service is currently unavailable (circuit open)", ex);
    }
}
