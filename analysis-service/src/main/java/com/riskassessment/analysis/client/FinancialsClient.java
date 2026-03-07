package com.riskassessment.analysis.client;

import com.riskassessment.analysis.dto.FinancialDataDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", contextId = "companyFinancialsClient")
public interface FinancialsClient {

    @GetMapping("/api/v1/companies/{companyId}/financials/latest")
    FinancialDataDTO getLatestFinancials(@PathVariable("companyId") Long companyId);
}
