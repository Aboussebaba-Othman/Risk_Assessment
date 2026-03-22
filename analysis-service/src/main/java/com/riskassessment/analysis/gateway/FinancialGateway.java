package com.riskassessment.analysis.gateway;

import com.riskassessment.analysis.client.FinancialsClient;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancialGateway {

    private final FinancialsClient financialsClient;

    @CircuitBreaker(name = "company-service", fallbackMethod = "fallbackGetLatestFinancials")
    public FinancialDataDTO getLatestFinancials(Long companyId) {
        log.info("Fetching strict financial data for companyID: {}", companyId);
        try {
            return financialsClient.getLatestFinancials(companyId);
        } catch (Exception e) {
            log.warn("Failed to communicate with financials-service for companyId={}. Error: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Failed to fetch financials from external service", e);
        }
    }

    public FinancialDataDTO fallbackGetLatestFinancials(Long companyId, Exception ex) {
        log.error("Circuit breaker OPEN for company-service [getLatestFinancials] companyId={}: {}", companyId, ex.getMessage());
        throw new ExternalServiceException("company-service is currently unavailable (circuit open)", ex);
    }
}
