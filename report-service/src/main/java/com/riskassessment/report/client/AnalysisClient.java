package com.riskassessment.report.client;

import com.riskassessment.report.dto.AnalysisDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "analysis-service")
public interface AnalysisClient {

    @PostMapping("/analysis/companies/{companyId}/trigger")
    AnalysisDTO performSwotAnalysis(@PathVariable("companyId") Long companyId);
}
