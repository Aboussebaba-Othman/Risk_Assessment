package com.riskassessment.scoring.service;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;

import java.util.List;

public interface IScoringService {
    Score calculateScore(Long companyId);
    Score getLatestScore(Long companyId);
    List<Score> getScoreHistory(Long companyId);
    List<Score> getAllScores();
    RecommendationDTO getRecommendation(Long companyId);
}
