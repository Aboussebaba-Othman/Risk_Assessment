package com.riskassessment.alertservice.client;

import com.riskassessment.alertservice.dto.ScoreDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "scoring-service")
public interface ScoringClient {

    @GetMapping("/api/scoring/companies/{companyId}/history")
    List<ScoreDTO> getScoreHistory(@PathVariable Long companyId);
}
