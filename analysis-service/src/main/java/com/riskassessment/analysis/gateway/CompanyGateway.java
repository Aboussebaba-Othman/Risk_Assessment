package com.riskassessment.analysis.gateway;

import com.riskassessment.analysis.client.CompanyClient;
import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyGateway {

    private final CompanyClient companyClient;

    public CompanyDTO getCompanyById(Long companyId) {
        log.info("Fetching company metadata for ID: {}", companyId);
        try {
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.error("Failed to communicate with company-service for companyId={}. Error: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Failed to fetch company details from external service", e);
        }
    }
}
