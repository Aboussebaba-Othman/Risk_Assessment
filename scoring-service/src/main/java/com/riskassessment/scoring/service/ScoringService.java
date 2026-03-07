package com.riskassessment.scoring.service;

import com.riskassessment.scoring.client.AlertClient;
import com.riskassessment.scoring.client.CompanyClient;
import com.riskassessment.scoring.dto.*;
import com.riskassessment.scoring.engine.RecommendationEngine;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.entity.enums.RiskLevel;
import com.riskassessment.scoring.entity.enums.RiskRating;
import com.riskassessment.scoring.repository.ScoreRepository;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringService {

    private final ScoreRepository scoreRepository;
    private final CompanyClient companyClient;
    private final AlertClient alertClient;
    private final ScoringStrategy scoringStrategy;
    private final RecommendationEngine recommendationEngine;

    public Score calculateScore(Long companyId) {
        log.info("Starting CDC-compliant score calculation for companyId: {}", companyId);

        // 1. Fetch financial data from company-service
        CompanyFinancialsDTO raw = companyClient.getLatestFinancialData(companyId);
        FinancialDataDTO financials = mapToFinancialDataDTO(raw);

        // 2. Fetch company info (sector, incorporation date, etc.)
        CompanyDTO company = companyClient.getCompanyInfo(companyId);

        // 3. Calculate score via CDC 15-ratio strategy
        ScoringResult result = scoringStrategy.calculate(company, financials);
        int calculatedScore = result.getFinalScore();
        RiskLevel riskLevel = determineRiskLevel(calculatedScore);

        // 4. Persist score with all sub-scores
        Score score = new Score();
        score.setCompanyId(companyId);
        score.setTenantId(company.getTenantId() != null ? company.getTenantId() : 1L);
        score.setOverallScore(new BigDecimal(calculatedScore));
        score.setFinancialScore(new BigDecimal(result.getFinancialScore())); // 40% — santé financière
        score.setOperationalScore(new BigDecimal(result.getPaymentScore())); // 35% — comportement paiement
        score.setMarketScore(new BigDecimal(result.getContextScore())); // 25% — contexte
        score.setLegalScore(BigDecimal.ZERO); // non utilisé CDC

        int confidence = 75;
        if (financials.getTotalPayments() != null && financials.getTotalPayments() > 0)
            confidence += 15;
        if (financials.getShareCapital() != null)
            confidence += 5;
        if (financials.getEbitda() != null)
            confidence += 5;
        score.setConfidenceLevel(new BigDecimal(Math.min(100, confidence)));

        // Score valid for 12 months
        score.setValidUntil(LocalDateTime.now().plusYears(1));

        score.setRiskLevel(riskLevel);
        score.setScoredAt(LocalDateTime.now());
        score.setRiskRating(determineRiskRating(calculatedScore));
        score.setCreatedAt(LocalDateTime.now());
        score.setUpdatedAt(LocalDateTime.now());
        String notes = "Score CDC — 15 ratios. Niveau: " + riskLevel.name()
                + (result.getScoringNotes() != null ? " | " + result.getScoringNotes() : "");
        score.setNotes(notes);

        Score savedScore = scoreRepository.save(score);
        log.info("Score saved: {} → {} ({}) fin={} pay={} ctx={}",
                companyId, calculatedScore, riskLevel,
                result.getFinancialScore(), result.getPaymentScore(), result.getContextScore());

        // 5. Trigger alert if HIGH or CRITICAL
        if (riskLevel == RiskLevel.HIGH_RISK || riskLevel == RiskLevel.CRITICAL) {
            triggerRiskAlert(companyId, calculatedScore, riskLevel);
        }

        return savedScore;
    }

    public Score getLatestScore(Long companyId) {
        return scoreRepository.findTopByCompanyIdOrderByScoredAtDesc(companyId)
                .orElseThrow(() -> new RuntimeException("No score found for company ID: " + companyId));
    }

    public List<Score> getScoreHistory(Long companyId) {
        return scoreRepository.findByCompanyIdOrderByScoredAtDesc(companyId);
    }

    public RecommendationDTO getRecommendation(Long companyId) {
        Score latestScore = getLatestScore(companyId);
        int score = latestScore.getOverallScore().intValue();
        RiskLevel riskLevel = latestScore.getRiskLevel();
        return recommendationEngine.recommendWithJustification(score, riskLevel, List.of());
    }

    // PRIVATE HELPERS

    private FinancialDataDTO mapToFinancialDataDTO(CompanyFinancialsDTO src) {
        if (src == null)
            return new FinancialDataDTO();
        return FinancialDataDTO.builder()
                // Actif
                .totalAssets(src.getTotalAssets())
                .currentAssets(src.getCurrentAssets())
                .fixedAssets(src.getFixedAssets())
                .inventory(src.getInventory())
                .accountsReceivable(src.getAccountsReceivable())
                .cash(src.getCash())
                // Passif
                .totalLiabilities(src.getTotalLiabilities())
                .currentLiabilities(src.getCurrentLiabilities())
                .longTermDebt(src.getLongTermDebt())
                .accountsPayable(src.getAccountsPayable())
                .equity(src.getEquity())
                // Résultat
                .revenue(src.getRevenue())
                .netResult(src.getNetResult())
                .operatingIncome(src.getOperatingIncome())
                .financialExpenses(src.getFinancialExpenses())
                .ebitda(src.getEbitda())
                .costOfGoodsSold(src.getCostOfGoodsSold())
                // Paiement
                .totalPayments(src.getTotalPayments())
                .onTimePayments(src.getOnTimePayments())
                .latePayments(src.getLatePayments())
                .averagePaymentDelay(src.getAveragePaymentDelay())
                .unpaidCount(src.getUnpaidCount())
                .litigationCount(src.getLitigationCount())
                // Contexte
                .shareCapital(src.getShareCapital())
                .employeeCount(src.getEmployeeCount())
                .build();
    }

    // Risk Level mapping (section 3.3).
    private RiskLevel determineRiskLevel(int score) {
        if (score >= 90)
            return RiskLevel.EXCELLENT;
        if (score >= 75)
            return RiskLevel.LOW_RISK;
        if (score >= 60)
            return RiskLevel.MODERATE_RISK;
        if (score >= 40)
            return RiskLevel.MEDIUM_RISK;
        if (score >= 25)
            return RiskLevel.HIGH_RISK;
        return RiskLevel.CRITICAL;
    }

    // Standard rating scale for reporting (AAA..D).
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

    private void triggerRiskAlert(Long companyId, int score, RiskLevel level) {
        try {
            AlertRequestDTO alertRequest = AlertRequestDTO.builder()
                    .recipient("risk@riskassessment.com")
                    .subject(String.format("ALERTE RISQUE %s — Société #%d", level.name(), companyId))
                    .message(String.format(
                            "Le score de la société #%d est tombé à %d/100 (Niveau: %s). " +
                                    "Une action immédiate est requise.",
                            companyId, score, level.name()))
                    .type("SCORE_CHANGE")
                    .build();
            alertClient.triggerAlert(alertRequest);
            log.info("Risk alert triggered for company {} (score={}, level={})", companyId, score, level);
        } catch (Exception e) {
            log.error("Failed to trigger alert for company {}: {}", companyId, e.getMessage());
        }
    }
}
