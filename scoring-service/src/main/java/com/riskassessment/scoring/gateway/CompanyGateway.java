package com.riskassessment.scoring.gateway;

import com.riskassessment.scoring.client.CompanyClient;
import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.CompanyFinancialsDTO;
import com.riskassessment.scoring.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyGateway {

    private final CompanyClient companyClient;

    public CompanyFinancialsDTO getLatestFinancialData(Long companyId) {
        try {
            return companyClient.getLatestFinancialData(companyId);
        } catch (Exception e) {
            log.error("Failed to retrieve financial data from company-service for companyId {}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Failed to request financials from company-service", e);
        }
    }

    public CompanyDTO getCompanyInfo(Long companyId) {
        try {
            return companyClient.getCompanyInfo(companyId);
        } catch (Exception e) {
            log.error("Failed to retrieve general company info from company-service for companyId {}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Failed to request core company identity from company-service", e);
        }
    }
}
