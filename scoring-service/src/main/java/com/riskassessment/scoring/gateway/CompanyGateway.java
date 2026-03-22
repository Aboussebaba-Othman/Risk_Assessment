package com.riskassessment.scoring.gateway;

import com.riskassessment.scoring.client.CompanyClient;
import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.CompanyFinancialsDTO;
import com.riskassessment.scoring.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyGateway {

    private final CompanyClient companyClient;

    @CircuitBreaker(name = "company-service", fallbackMethod = "fallbackGetCompanyInfo")
    public CompanyDTO getCompanyInfo(Long companyId) {
        try {
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch company info for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve company info from company-service", e);
        }
    }

    @CircuitBreaker(name = "company-service", fallbackMethod = "fallbackGetLatestFinancials")
    public CompanyFinancialsDTO getLatestFinancialData(Long companyId) {
        try {
            return companyClient.getLatestFinancialData(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch financial data for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve financial data from company-service", e);
        }
    }

    public CompanyDTO fallbackGetCompanyInfo(Long companyId, Exception ex) {
        log.error("Circuit breaker OPEN for company-service [getCompanyInfo] companyId={}: {}", companyId, ex.getMessage());
        throw new ExternalServiceException("company-service is currently unavailable (circuit open)", ex);
    }

    public CompanyFinancialsDTO fallbackGetLatestFinancials(Long companyId, Exception ex) {
        log.error("Circuit breaker OPEN for company-service [getLatestFinancials] companyId={}: {}", companyId, ex.getMessage());
        throw new ExternalServiceException("company-service is currently unavailable (circuit open)", ex);
    }
}
