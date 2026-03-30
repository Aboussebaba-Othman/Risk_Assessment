package com.riskassessment.scoring.service;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;

import java.util.List;

public interface IScoringService {
    Score calculateScore(Long companyId, Long tenantId);
    Score getLatestScore(Long companyId, Long tenantId);
    List<Score> getScoreHistory(Long companyId, Long tenantId);
    List<Score> getAllScores(Long tenantId);
    RecommendationDTO getRecommendation(Long companyId, Long tenantId);
}
