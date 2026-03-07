package com.riskassessment.scoring.controller;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.service.ScoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/scores")
@RequiredArgsConstructor
public class ScoringController {

    private final ScoringService scoringService;

    
    @PostMapping("/calculate/{companyId}")
    public ResponseEntity<Score> calculateScore(@PathVariable Long companyId) {
        return ResponseEntity.ok(scoringService.calculateScore(companyId));
    }

    
    @GetMapping("/companies/{companyId}/latest")
    public ResponseEntity<Score> getLatestScore(@PathVariable Long companyId) {
        return ResponseEntity.ok(scoringService.getLatestScore(companyId));
    }

    
    @GetMapping("/companies/{companyId}/recommendation")
    public ResponseEntity<RecommendationDTO> getRecommendation(@PathVariable Long companyId) {
        return ResponseEntity.ok(scoringService.getRecommendation(companyId));
    }


    @GetMapping("/companies/{companyId}/history")
    public ResponseEntity<List<Score>> getScoreHistory(@PathVariable Long companyId) {
        return ResponseEntity.ok(scoringService.getScoreHistory(companyId));
    }
}
