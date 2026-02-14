package com.riskassessment.analysis.client;

import com.riskassessment.analysis.dto.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Client to communicate with Company Service.
 * Retrieving Company details mainly.
 */
@FeignClient(name = "company-service", url = "${application.config.company-service-url:http://localhost:8082}")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{companyId}")
    CompanyDTO getCompanyById(@PathVariable("companyId") Long companyId);
}
