package com.riskassessment.scoring.controller;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.security.SecurityUtils;
import com.riskassessment.scoring.service.IScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/api/scoring", "/api/v1/scores"})
@RequiredArgsConstructor
@Slf4j
public class ScoringController {

    private final IScoringService scoringService;

    @PostMapping("/calculate/{companyId}")
    public ResponseEntity<Score> calculateScore(@PathVariable Long companyId) {
        Long tenantId = SecurityUtils.getTenantId();
        log.info("Request to calculate score for companyId={} from tenantId={}", companyId, tenantId);
        return ResponseEntity.ok(scoringService.calculateScore(companyId, tenantId));
    }

    @GetMapping("/companies/{companyId}/latest")
    public ResponseEntity<Score> getLatestScore(@PathVariable Long companyId) {
        Long tenantId = SecurityUtils.getTenantId();
        return ResponseEntity.ok(scoringService.getLatestScore(companyId, tenantId));
    }

    @GetMapping("/companies/{companyId}/recommendation")
    public ResponseEntity<RecommendationDTO> getRecommendation(@PathVariable Long companyId) {
        Long tenantId = SecurityUtils.getTenantId();
        return ResponseEntity.ok(scoringService.getRecommendation(companyId, tenantId));
    }

    @GetMapping("/companies/{companyId}/history")
    public ResponseEntity<List<Score>> getScoreHistory(@PathVariable Long companyId) {
        Long tenantId = SecurityUtils.getTenantId();
        return ResponseEntity.ok(scoringService.getScoreHistory(companyId, tenantId));
    }

    @GetMapping
    public ResponseEntity<List<Score>> getAllScores() {
        Long tenantId = SecurityUtils.getTenantId();
        log.info("Request to get all scores for tenantId={}", tenantId);
        return ResponseEntity.ok(scoringService.getAllScores(tenantId));
    }
}
