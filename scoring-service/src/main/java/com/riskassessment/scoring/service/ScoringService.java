package com.riskassessment.scoring.service;

import com.riskassessment.scoring.client.CompanyClient;
import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.repository.ScoreRepository;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.riskassessment.scoring.entity.enums.RiskRating;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final ScoreRepository scoreRepository;
    private final CompanyClient companyClient;
    private final ScoringStrategy scoringStrategy;

    public Score calculateScore(Long companyId) {
        log.info("Starting score calculation for companyId: {}", companyId);
        CompanyDTO company = new CompanyDTO();
        company.setId(companyId);

        com.riskassessment.scoring.dto.CompanyFinancialsDTO clientFinancials = companyClient
                .getLatestFinancialData(companyId);
        FinancialDataDTO financials = mapToFinancialDataDTO(clientFinancials);

        // 3. Calculate Score using Strategy
        int calculatedScore = scoringStrategy.calculate(company, financials);
        String riskLevelStr = determineRiskLevel(calculatedScore);

        Score score = new Score();
        score.setCompanyId(companyId);
        score.setTenantId(1L);
        score.setOverallScore(new BigDecimal(calculatedScore));
        score.setRiskLevel(com.riskassessment.scoring.entity.enums.RiskLevel.valueOf(riskLevelStr));
        score.setScoredAt(LocalDateTime.now());
        score.setRiskRating(determineRiskRating(calculatedScore));
        score.setUpdatedAt(LocalDateTime.now());
        score.setCreatedAt(LocalDateTime.now());

        score.setNotes("Revenue: " + (financials != null ? financials.getRevenue() : "N/A"));

        Score savedScore = scoreRepository.save(score);
        log.info("Score calculated and saved: {} (Risk: {})", calculatedScore, riskLevelStr);

        return savedScore;
    }

    private FinancialDataDTO mapToFinancialDataDTO(com.riskassessment.scoring.dto.CompanyFinancialsDTO source) {
        if (source == null)
            return null;
        return FinancialDataDTO.builder()
                .revenue(source.getRevenue())
                .netResult(source.getNetResult())
                .build();
    }

    public Score getLatestScore(Long companyId) {
        return scoreRepository.findTopByCompanyIdOrderByCalculatedAtDesc(companyId)
                .orElseThrow(() -> new RuntimeException("No score found for company ID: " + companyId));
    }

    private String determineRiskLevel(int score) {
        if (score >= 80)
            return "LOW";
        if (score >= 50)
            return "MEDIUM";
        return "HIGH";
    }

    private RiskRating determineRiskRating(int score) {
        if (score >= 95)
            return RiskRating.AAA;
        if (score >= 90)
            return RiskRating.AA;
        if (score >= 85)
            return RiskRating.A;
        if (score >= 75)
            return RiskRating.BBB;
        if (score >= 65)
            return RiskRating.BB;
        if (score >= 55)
            return RiskRating.B;
        if (score >= 45)
            return RiskRating.CCC;
        if (score >= 35)
            return RiskRating.CC;
        if (score >= 25)
            return RiskRating.C;
        return RiskRating.D;
    }
}
