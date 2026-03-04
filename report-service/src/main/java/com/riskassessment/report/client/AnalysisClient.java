package com.riskassessment.report.client;

import com.riskassessment.report.dto.AnalysisDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "analysis-service", url = "${application.config.analysis-service-url:http://localhost:8085}")
public interface AnalysisClient {

    @GetMapping("/api/v1/analysis/swot/{companyId}")
    AnalysisDTO performSwotAnalysis(@PathVariable("companyId") Long companyId);
}
