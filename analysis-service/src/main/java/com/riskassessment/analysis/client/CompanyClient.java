package com.riskassessment.analysis.client;

import com.riskassessment.analysis.dto.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", contextId = "companyDetailsClient")
public interface CompanyClient {

    @GetMapping("/companies/{companyId}")
    CompanyDTO getCompanyById(@PathVariable("companyId") Long companyId);
}
