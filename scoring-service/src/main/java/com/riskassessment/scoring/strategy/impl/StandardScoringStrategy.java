package com.riskassessment.scoring.strategy.impl;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.dto.ScoringResult;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import com.riskassessment.scoring.strategy.scoring.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class StandardScoringStrategy implements ScoringStrategy {

    private final LiquidityScorer liquidityScorer;
    private final SolvencyScorer solvencyScorer;
    private final ProfitabilityScorer profitabilityScorer;
    private final ManagementScorer managementScorer;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final Map<String, Integer> SECTOR_ADJUSTMENTS = Map.ofEntries(
            Map.entry("HEALTH", 5), Map.entry("PHARMACY", 5),
            Map.entry("SANTE", 5), Map.entry("PHARMACIE", 5),
            Map.entry("IT", 3), Map.entry("TECH", 3),
            Map.entry("SOFTWARE", 3), Map.entry("INFORMATIQUE", 3),
            Map.entry("FOOD", 2), Map.entry("AGRO", 2), Map.entry("AGROALIMENTAIRE", 2),
            Map.entry("TRADE", 0), Map.entry("COMMERCE", 0), Map.entry("RETAIL", 0),
            Map.entry("BTP", -7), Map.entry("CONSTRUCTION", -7));

    @Override
    public ScoringResult calculate(CompanyDTO company, FinancialDataDTO f) {
        if (isUnderCollectiveProcedure(f)) {
            log.warn("CAS-06: Collective procedure detected — score forced to 0");
            return zeroScore("CAS-06: Procédure collective détectée");
        }
        if (hasNegativeEquity(f)) {
            log.warn("CAS-04: Negative equity detected — score forced to 24 (CRITICAL)");
            return ScoringResult.builder().finalScore(24).financialScore(0).paymentScore(50).contextScore(50)
                    .scoringNotes("CAS-04: Capitaux propres négatifs").build();
        }

        int computed = countComputedRatios(f);
        if (computed < 7) {
            log.warn("CAS-02: Only {} ratios computable — analysis blocked", computed);
            return zeroScore("CAS-02: Données insuffisantes (" + computed + " ratios)");
        }
        boolean partial = computed < 10;

        double sante = calculateSanteFinanciere(f, partial);
        String notes = null;

        if (hasNegativeNetResult(f)) {
            sante = Math.max(0, sante - 5);
            notes = "CAS-03: Résultat net négatif (-5 pts)";
            log.debug("CAS-03: Negative net result — -5 penalty on health score");
        }

        double payment = calculatePaymentScore(f);
        double context = calculateContextScore(company, f);
        double finalScore = (sante * 0.40) + (payment * 0.35) + (context * 0.25);

        if (isVeryYoung(company)) {
            finalScore = Math.min(finalScore, 65.0);
            notes = (notes != null ? notes + " | " : "") + "CAS-01: Entreprise < 2 ans (plafonné à 65)";
            log.debug("CAS-01: Young company (<2 yrs) — score capped at 65");
        }

        int result = clamp(finalScore);
        log.info("Score calc: ratios={} sante={} payment={} context={} => FINAL={}",
                computed, (int) sante, (int) payment, (int) context, result);

        return ScoringResult.builder()
                .finalScore(result)
                .financialScore(clamp(sante))
                .paymentScore(clamp(payment))
                .contextScore(clamp(context))
                .scoringNotes(notes)
                .build();
    }
    // FINANCIAL HEALTH (weight = 40%)

    private double calculateSanteFinanciere(FinancialDataDTO f, boolean partial) {
        double liq  = liquidityScorer.score(f, partial);
        double solv  = solvencyScorer.score(f, partial);
        double rent  = profitabilityScorer.score(f, partial);
        double gest  = managementScorer.gestScore(f, partial);
        double stru  = managementScorer.structureScore(f, partial);
        return (liq * 0.20 + solv * 0.20 + rent * 0.30 + gest * 0.20 + stru * 0.10) * 10.0;
    }

    // PAYMENT BEHAVIOUR (weight = 35%)
    private double calculatePaymentScore(FinancialDataDTO f) {
        if (f.getTotalPayments() == null || f.getTotalPayments() == 0) {
            log.debug("CAS-05: No payment history — neutral score 50");
            return 50.0;
        }
        double base            = ((double) safe(f.getOnTimePayments()) / f.getTotalPayments()) * 100.0;
        double retardPenalty   = safe(f.getLatePayments()) * 2.0;
        if (f.getAveragePaymentDelay() != null && f.getAveragePaymentDelay() > 30) {
            retardPenalty += (f.getAveragePaymentDelay() - 30) * 0.3;
        }
        double unpaidPenalty    = safe(f.getUnpaidCount()) * 10.0;
        double litigationPenalty = safe(f.getLitigationCount()) * 15.0;
        return Math.min(100, Math.max(0, base - retardPenalty - unpaidPenalty - litigationPenalty));
    }

    // CONTEXT SCORE (weight = 25%)
    private double calculateContextScore(CompanyDTO company, FinancialDataDTO f) {
        double base = 50.0;
        base += getSectorAdj(company.getIndustrySector());
        base += getAgeAdj(company.getIncorporationDate());
        base += getSizeBonus(f.getEmployeeCount());
        base += getCapitalBonus(f.getShareCapital());
        return Math.min(100, Math.max(0, base));
    }

    private int getSectorAdj(String sector) {
        if (sector == null) return 0;
        String up = sector.toUpperCase().trim();
        for (Map.Entry<String, Integer> e : SECTOR_ADJUSTMENTS.entrySet()) {
            if (up.contains(e.getKey())) return e.getValue();
        }
        return 0;
    }

    private int getAgeAdj(LocalDate inception) {
        if (inception == null) return 0;
        int years = Period.between(inception, LocalDate.now()).getYears();
        if (years >= 10) return 5;
        if (years >= 5)  return 0;
        if (years >= 2)  return -5;
        if (years >= 1)  return -10;
        return -15;
    }

    private int getSizeBonus(Integer emp) {
        if (emp == null) return 0;
        if (emp >= 500) return 10;
        if (emp >= 100) return 7;
        if (emp >= 50)  return 4;
        if (emp >= 10)  return 2;
        return 0;
    }

    private int getCapitalBonus(BigDecimal capital) {
        if (capital == null) return 0;
        double v = capital.doubleValue();
        if (v >= 5_000_000) return 5;
        if (v >= 1_000_000) return 3;
        if (v >= 100_000)   return 1;
        return 0;
    }

    // EDGE CASE DETECTION
    private boolean isUnderCollectiveProcedure(FinancialDataDTO f) {
        return f.getLitigationCount() != null && f.getLitigationCount() >= 3;
    }

    private boolean hasNegativeEquity(FinancialDataDTO f) {
        return f.getEquity() != null && f.getEquity().compareTo(ZERO) < 0;
    }

    private boolean hasNegativeNetResult(FinancialDataDTO f) {
        return f.getNetResult() != null && f.getNetResult().compareTo(ZERO) < 0;
    }

    private boolean isVeryYoung(CompanyDTO c) {
        if (c == null || c.getIncorporationDate() == null) return false;
        return Period.between(c.getIncorporationDate(), LocalDate.now()).getYears() < 2;
    }

    private int countComputedRatios(FinancialDataDTO f) {
        int n = 0;
        if (pos(f.getCurrentAssets()) && pos(f.getCurrentLiabilities())) n++; 
        if (pos(f.getCurrentLiabilities())) n++;                               
        if (pos(f.getCurrentLiabilities())) n++;                               
        if (pos(f.getTotalLiabilities())) n++;                               
        if (pos(f.getEquity())) n++;                               
        if (f.getOperatingIncome() != null) n++;                               
        if (pos(f.getTotalAssets()) && f.getNetResult() != null) n++;          
        if (pos(f.getRevenue()) && f.getNetResult() != null) n++;              
        if (pos(f.getEquity()) && f.getNetResult() != null)  n++;              
        if (pos(f.getRevenue()) && f.getEbitda() != null) n++;              
        if (pos(f.getRevenue()) && f.getAccountsReceivable() != null) n++;     
        if (f.getAccountsPayable() != null) n++;                               
        if (f.getInventory() != null) n++;                               
        if (pos(f.getRevenue())) n++;                               
        if (pos(f.getRevenue())) n++;                               
        return n;
    }

    // UTILS
    private int clamp(double v) {
        return (int) Math.round(Math.min(100, Math.max(0, v)));
    }

    private boolean pos(BigDecimal v) {
        return v != null && v.compareTo(ZERO) > 0;
    }

    private int safe(Integer v) {
        return v == null ? 0 : v;
    }

    private ScoringResult zeroScore(String notes) {
        return ScoringResult.builder().finalScore(0).financialScore(0).paymentScore(0).contextScore(0)
                .scoringNotes(notes).build();
    }
}
