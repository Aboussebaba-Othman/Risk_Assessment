package com.riskassessment.scoring.client;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.CompanyFinancialsDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service")
public interface CompanyClient {

    @GetMapping("/companies/{id}")
    CompanyDTO getCompanyById(@PathVariable("id") Long id);

    @GetMapping("/companies/{id}")
    CompanyDTO getCompanyInfo(@PathVariable("id") Long id);

    @GetMapping("/companies/{companyId}/financials/latest")
    CompanyFinancialsDTO getLatestFinancialData(@PathVariable("companyId") Long companyId);
}
