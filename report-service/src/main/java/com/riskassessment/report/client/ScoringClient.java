package com.riskassessment.report.client;

import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "scoring-service", url = "${application.config.scoring-service-url:http://localhost:8083}")
public interface ScoringClient {

    @GetMapping("/api/v1/scores/companies/{companyId}/latest")
    ScoreDTO getLatestScore(@PathVariable("companyId") Long companyId);
}
