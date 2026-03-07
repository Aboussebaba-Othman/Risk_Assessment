package com.riskassessment.scoring.client;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.CompanyFinancialsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{id}")
    CompanyDTO getCompanyById(@PathVariable("id") Long id);

    @GetMapping("/api/v1/companies/{id}")
    CompanyDTO getCompanyInfo(@PathVariable("id") Long id);

    @GetMapping("/api/v1/companies/{companyId}/financials/latest")
    CompanyFinancialsDTO getLatestFinancialData(@PathVariable("companyId") Long companyId);
}
