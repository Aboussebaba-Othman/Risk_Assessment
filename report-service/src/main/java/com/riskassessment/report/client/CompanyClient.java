package com.riskassessment.report.client;

import com.riskassessment.report.dto.CompanyDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "company-service", url = "${application.config.company-service-url:http://localhost:8082}")
public interface CompanyClient {

    @GetMapping("/api/v1/companies/{id}")
    CompanyDTO getCompanyById(@PathVariable("id") Long id);
}
