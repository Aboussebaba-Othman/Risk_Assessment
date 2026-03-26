package com.riskassessment.scoring.service.impl;

import com.riskassessment.scoring.gateway.AlertGateway;
import com.riskassessment.scoring.gateway.CompanyGateway;
import com.riskassessment.scoring.dto.*;
import com.riskassessment.scoring.engine.RecommendationEngine;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.enums.RiskLevel;
import com.riskassessment.scoring.enums.RiskRating;
import com.riskassessment.scoring.repository.ScoreRepository;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import com.riskassessment.scoring.exception.ResourceNotFoundException;
import com.riskassessment.scoring.mapper.ScoringMapper;
import com.riskassessment.scoring.security.SecurityUtils;
import com.riskassessment.scoring.service.IScoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScoringServiceImpl implements IScoringService {

    private final ScoreRepository scoreRepository;
    private final CompanyGateway companyGateway;
    private final AlertGateway alertGateway;
    private final ScoringStrategy scoringStrategy;
    private final RecommendationEngine recommendationEngine;
    private final ScoringMapper scoringMapper;

    public Score calculateScore(Long companyId) {
        log.info("Starting CDC-compliant score calculation for companyId: {}", companyId);

        // 1. Fetch financial data from company-service
        CompanyFinancialsDTO raw = companyGateway.getLatestFinancialData(companyId);
        FinancialDataDTO financials = scoringMapper.toFinancialDataDTO(raw);

        // 2. Fetch company info (sector, incorporation date, etc.)
        CompanyDTO company = companyGateway.getCompanyInfo(companyId);

        // 3. Calculate score via CDC 15-ratio strategy
        ScoringResult result = scoringStrategy.calculate(company, financials);
        int calculatedScore = result.getFinalScore();
        RiskLevel riskLevel = determineRiskLevel(calculatedScore);

        // 4. Persist score with all sub-scores
        Score score = new Score();
        score.setCompanyId(companyId);
        Long currentUserId = SecurityUtils.getCurrentUserId();
        score.setTenantId(company.getTenantId() != null ? company.getTenantId() : (currentUserId != null ? currentUserId : 1L));
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
            triggerRiskAlert(companyId, score.getTenantId(), calculatedScore, riskLevel);
        }

        return savedScore;
    }

    public Score getLatestScore(Long companyId) {
        return scoreRepository.findTopByCompanyIdOrderByScoredAtDesc(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("No score found for company ID: " + companyId));
    }

    public List<Score> getScoreHistory(Long companyId) {
        return scoreRepository.findByCompanyIdOrderByScoredAtDesc(companyId);
    }

    public List<Score> getAllScores() {
        return scoreRepository.findAll();
    }

    public RecommendationDTO getRecommendation(Long companyId) {
        Score latestScore = getLatestScore(companyId);
        
        // Fetch financial data to extract specific red flags
        CompanyFinancialsDTO raw = companyGateway.getLatestFinancialData(companyId);
        FinancialDataDTO financials = scoringMapper.toFinancialDataDTO(raw);
        
        List<String> warnings = new java.util.ArrayList<>();
        if (financials.getEquity() != null && financials.getEquity().compareTo(BigDecimal.ZERO) < 0) {
            warnings.add("Alerte: Capitaux propres négatifs détectés.");
        }
        if (financials.getLitigationCount() != null && financials.getLitigationCount() > 0) {
            warnings.add("Alerte: Présence de litiges actifs (" + financials.getLitigationCount() + ").");
        }
        if (financials.getNetResult() != null && financials.getNetResult().compareTo(BigDecimal.ZERO) < 0) {
            warnings.add("Alerte: Résultat net déficitaire sur le dernier exercice.");
        }
        if (financials.getLatePayments() != null && financials.getLatePayments() > 0) {
            warnings.add("Alerte: Historique de retards de paiement constaté.");
        }

        return recommendationEngine.recommendWithJustification(latestScore, warnings);
    }

    // PRIVATE HELPERS

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

    private void triggerRiskAlert(Long companyId, Long tenantId, int score, RiskLevel level) {
        AlertRequestDTO alertRequest = AlertRequestDTO.builder()
                .companyId(companyId)
                .tenantId(tenantId)
                .recipient("risk@riskassessment.com")
                .subject(String.format("ALERTE RISQUE %s — Société #%d", level.name(), companyId))
                .message(String.format(
                        "Le score de la société #%d est tombé à %d/100 (Niveau: %s). " +
                                "Une action immédiate est requise.",
                        companyId, score, level.name()))
                .type("SCORE_CHANGE")
                .severity(level == RiskLevel.CRITICAL ? "CRITICAL" : (level == RiskLevel.HIGH_RISK ? "HIGH" : "WARNING"))
                .build();
        alertGateway.triggerAlert(alertRequest);
        log.info("Risk alert triggered for company {} (tenantId={}, score={}, level={}, severity={})", 
                companyId, tenantId, score, level, alertRequest.getSeverity());
    }
}
