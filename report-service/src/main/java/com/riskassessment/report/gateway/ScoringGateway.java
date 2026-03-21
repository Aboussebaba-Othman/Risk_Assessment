package com.riskassessment.report.gateway;

import com.riskassessment.report.client.ScoringClient;
import com.riskassessment.report.dto.RecommendationDTO;
import com.riskassessment.report.dto.ScoreDTO;
import com.riskassessment.report.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoringGateway {

    private final ScoringClient scoringClient;

    public ScoreDTO getLatestScore(Long companyId) {
        try {
            return scoringClient.getLatestScore(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch score for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve score from scoring-service", e);
        }
    }

    public RecommendationDTO getRecommendation(Long companyId) {
        try {
            return scoringClient.getRecommendation(companyId);
        } catch (Exception e) {
            log.warn("Failed to fetch recommendation for companyId={}: {}", companyId, e.getMessage());
            throw new ExternalServiceException("Cannot retrieve recommendation from scoring-service", e);
        }
    }
}
