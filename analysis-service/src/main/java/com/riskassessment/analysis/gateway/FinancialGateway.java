package com.riskassessment.analysis.gateway;

import com.riskassessment.analysis.client.FinancialsClient;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinancialGateway {

    private final FinancialsClient financialsClient;

    public FinancialDataDTO getLatestFinancials(Long companyId) {
        log.info("Fetching strict financial data for companyID: {}", companyId);
        try {
            return financialsClient.getLatestFinancials(companyId);
        } catch (Exception e) {
            log.warn("Failed to communicate with financials-service for companyId={}. This may cause incomplete SWOT calculations. Error: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Failed to fetch financials from external service", e);
        }
    }
}
