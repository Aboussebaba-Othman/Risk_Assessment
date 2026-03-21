package com.riskassessment.report.client;

import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "scoring-service")
public interface ScoringClient {

    @GetMapping("/api/scoring/companies/{companyId}/latest")
    ScoreDTO getLatestScore(@PathVariable("companyId") Long companyId);

    @GetMapping("/api/scoring/companies/{companyId}/recommendation")
    RecommendationDTO getRecommendation(@PathVariable("companyId") Long companyId);
}
