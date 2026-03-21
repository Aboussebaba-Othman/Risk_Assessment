package com.riskassessment.report.gateway;

import com.riskassessment.report.client.CompanyClient;
import com.riskassessment.report.dto.CompanyDTO;
import com.riskassessment.report.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompanyGateway {

    private final CompanyClient companyClient;

    public CompanyDTO getCompanyById(Long companyId) {
        try {
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch company data for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve company data from company-service", e);
        }
    }
}
