package com.riskassessment.scoring.strategy.impl;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.dto.ScoringResult;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

@Component
@Slf4j
public class StandardScoringStrategy implements ScoringStrategy {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final Map<String, Integer> SECTOR_ADJUSTMENTS = Map.ofEntries(
            Map.entry("HEALTH", 5),
            Map.entry("PHARMACY", 5),
            Map.entry("SANTE", 5),
            Map.entry("PHARMACIE", 5),
            Map.entry("IT", 3),
            Map.entry("TECH", 3),
            Map.entry("SOFTWARE", 3),
            Map.entry("INFORMATIQUE", 3),
            Map.entry("FOOD", 2),
            Map.entry("AGRO", 2),
            Map.entry("AGROALIMENTAIRE", 2),
            Map.entry("TRADE", 0),
            Map.entry("COMMERCE", 0),
            Map.entry("RETAIL", 0),
            Map.entry("BTP", -7),
            Map.entry("CONSTRUCTION", -7));

    private static final double LG_MIN = 0.5, LG_MAX = 3.0;
    private static final double LR_MIN = 0.3, LR_MAX = 2.0;
    private static final double LI_MIN = 0.0, LI_MAX = 1.0;
    private static final double AF_MIN = 0.0, AF_MAX = 0.7;
    private static final double CE_MIN = 0.0, CE_MAX = 3.0;
    private static final double CI_MIN = 1.0, CI_MAX = 10.0;
    private static final double ROA_MIN = -0.05, ROA_MAX = 0.20;
    private static final double MN_MIN = -0.05, MN_MAX = 0.20;
    private static final double ROE_MIN = -0.10, ROE_MAX = 0.30;
    private static final double EBITDA_MIN = 0.0, EBITDA_MAX = 0.30;
    private static final double DSO_MIN = 30.0, DSO_MAX = 180.0;
    private static final double DF_MIN = 15.0, DF_MAX = 120.0;
    private static final double RS_MIN = 0.0, RS_MAX = 12.0;
    private static final double FRN_MIN = -0.20, FRN_MAX = 0.30;
    private static final double BFR_MIN = 0.0, BFR_MAX = 0.20;

    @Override
    public ScoringResult calculate(CompanyDTO company, FinancialDataDTO f) {
        if (isUnderCollectiveProcedure(f)) {
            log.warn("CAS-06: Collective procedure detected — score forced to 0");
            return ScoringResult.builder().finalScore(0).financialScore(0).paymentScore(0).contextScore(0)
                    .scoringNotes("CAS-06: Procédure collective détectée").build();
        }
        if (hasNegativeEquity(f)) {
            log.warn("CAS-04: Negative equity detected — score forced to 24 (CRITICAL)");
            return ScoringResult.builder().finalScore(24).financialScore(0).paymentScore(50).contextScore(50)
                    .scoringNotes("CAS-04: Capitaux propres négatifs").build();
        }

        int computed = countComputedRatios(f);
        if (computed < 7) {
            log.warn("CAS-02: Only {} ratios computable — analysis blocked", computed);
            return ScoringResult.builder().finalScore(0).financialScore(0).paymentScore(0).contextScore(0)
                    .scoringNotes("CAS-02: Données insuffisantes (" + computed + " ratios)").build();
        }
        boolean partial = computed < 10;

        // 1. Santé Financière (contributes 40%)
        double sante = calculateSanteFinanciere(f, partial);
        String notes = null;

        // CAS-03: Résultat net négatif → -5 penalty
        if (hasNegativeNetResult(f)) {
            sante = Math.max(0, sante - 5);
            notes = "CAS-03: Résultat net négatif (-5 pts)";
            log.debug("CAS-03: Negative net result — -5 penalty on health score");
        }

        // 2. Comportement Paiement (contributes 35%)
        double payment = calculatePaymentScore(f);

        // 3. Contexte (contributes 25%)
        double context = calculateContextScore(company, f);

        double finalScore = (sante * 0.40) + (payment * 0.35) + (context * 0.25);

        // CAS-01: Entreprise < 2 ans → plafond à 65
        if (isVeryYoung(company)) {
            finalScore = Math.min(finalScore, 65.0);
            notes = (notes != null ? notes + " | " : "") + "CAS-01: Entreprise < 2 ans (plafonné à 65)";
            log.debug("CAS-01: Young company (<2 yrs) — score capped at 65");
        }

        int result = (int) Math.round(Math.min(100, Math.max(0, finalScore)));
        log.info("Score calc: ratios={} sante={} payment={} context={} => FINAL={}",
                computed, (int) sante, (int) payment, (int) context, result);

        return ScoringResult.builder()
                .finalScore(result)
                .financialScore((int) Math.round(Math.min(100, Math.max(0, sante))))
                .paymentScore((int) Math.round(Math.min(100, Math.max(0, payment))))
                .contextScore((int) Math.round(Math.min(100, Math.max(0, context))))
                .scoringNotes(notes)
                .build();
    }

    // 1. SANTÉ FINANCIÈRE (0-100, weight=40%)

    private double calculateSanteFinanciere(FinancialDataDTO f, boolean partial) {
        double liq = avg(ratioLG(f, partial), ratioLR(f, partial), ratioLI(f, partial));
        double solv = avg(ratioAF(f, partial), ratioCE(f, partial), ratioCI(f, partial));
        double rent = avg(ratioROA(f, partial), ratioMN(f, partial), ratioROE(f, partial), ratioEBITDA(f, partial));
        double gest = avg(ratioDSO(f, partial), ratioDF(f, partial), ratioRS(f, partial));
        double stru = avg(ratioFRN(f, partial), ratioBFR(f, partial));

        return (liq * 0.20 + solv * 0.20 + rent * 0.30 + gest * 0.20 + stru * 0.10) * 10.0;
    }

    // ── Liquidité ─────────────────────────────────────────────────────────────
    // R1: Actif Circulant / Passif Circulant
    private double ratioLG(FinancialDataDTO f, boolean p) {
        if (!pos(f.getCurrentLiabilities()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getCurrentAssets()) / bd(f.getCurrentLiabilities()), LG_MIN, LG_MAX, false);
    }

    // R2: (Actif Circulant - Stocks) / Passif Circulant
    private double ratioLR(FinancialDataDTO f, boolean p) {
        if (!pos(f.getCurrentLiabilities()))
            return p ? 5.0 : 0.0;
        return lerp((bd(f.getCurrentAssets()) - bd(f.getInventory())) / bd(f.getCurrentLiabilities()), LR_MIN, LR_MAX,
                false);
    }

    // R3: Trésorerie / Passif Circulant
    private double ratioLI(FinancialDataDTO f, boolean p) {
        if (!pos(f.getCurrentLiabilities()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getCash()) / bd(f.getCurrentLiabilities()), LI_MIN, LI_MAX, false);
    }

    // ── Solvabilité ───────────────────────────────────────────────────────────
    // R4: Capitaux Propres / Total Passif
    private double ratioAF(FinancialDataDTO f, boolean p) {
        if (!pos(f.getTotalLiabilities()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getEquity()) / bd(f.getTotalLiabilities()), AF_MIN, AF_MAX, false);
    }

    // R5: Total Dettes / Capitaux Propres (inverted — lower is better)
    private double ratioCE(FinancialDataDTO f, boolean p) {
        if (!pos(f.getEquity()))
            return 0.0;
        double debt = bd(f.getLongTermDebt()) + bd(f.getCurrentLiabilities());
        return lerp(debt / bd(f.getEquity()), CE_MIN, CE_MAX, true);
    }

    // R6: Résultat Exploitation / Charges Financières
    private double ratioCI(FinancialDataDTO f, boolean p) {
        if (!pos(f.getFinancialExpenses()))
            return 5.0; // no interest charges = neutral
        return lerp(bd(f.getOperatingIncome()) / bd(f.getFinancialExpenses()), CI_MIN, CI_MAX, false);
    }

    // ── Rentabilité ───────────────────────────────────────────────────────────
    // R7: ROA = Résultat Net / Total Actif
    private double ratioROA(FinancialDataDTO f, boolean p) {
        if (!pos(f.getTotalAssets()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getTotalAssets()), ROA_MIN, ROA_MAX, false);
    }

    // R8: Marge Nette = Résultat Net / CA
    private double ratioMN(FinancialDataDTO f, boolean p) {
        if (!pos(f.getRevenue()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getRevenue()), MN_MIN, MN_MAX, false);
    }

    // R9: ROE = Résultat Net / Capitaux Propres
    private double ratioROE(FinancialDataDTO f, boolean p) {
        if (!pos(f.getEquity()))
            return 0.0;
        return lerp(bd(f.getNetResult()) / bd(f.getEquity()), ROE_MIN, ROE_MAX, false);
    }

    // R10: EBITDA Margin = EBITDA / CA
    private double ratioEBITDA(FinancialDataDTO f, boolean p) {
        if (!pos(f.getRevenue()))
            return p ? 5.0 : 0.0;
        return lerp(bd(f.getEbitda()) / bd(f.getRevenue()), EBITDA_MIN, EBITDA_MAX, false);
    }

    // ── Gestion ───────────────────────────────────────────────────────────────
    // R11: DSO = Créances Clients / CA × 360 (inverted)
    private double ratioDSO(FinancialDataDTO f, boolean p) {
        if (!pos(f.getRevenue()))
            return p ? 5.0 : 0.0;
        return lerp((bd(f.getAccountsReceivable()) / bd(f.getRevenue())) * 360.0, DSO_MIN, DSO_MAX, true);
    }

    // R12: Délai Fournisseurs = Dettes Fourn. / Achats × 360
    private double ratioDF(FinancialDataDTO f, boolean p) {
        double purchases = bd(f.getCostOfGoodsSold()) > 0 ? bd(f.getCostOfGoodsSold()) : bd(f.getRevenue()) * 0.6;
        if (purchases <= 0)
            return p ? 5.0 : 0.0;
        return lerp((bd(f.getAccountsPayable()) / purchases) * 360.0, DF_MIN, DF_MAX, false);
    }

    // R13: Rotation Stocks = CA / Stocks
    private double ratioRS(FinancialDataDTO f, boolean p) {
        if (!pos(f.getInventory()))
            return 5.0; // service sector — neutral
        return lerp(bd(f.getRevenue()) / bd(f.getInventory()), RS_MIN, RS_MAX, false);
    }

    // ── Structure Financière ─────────────────────────────────────────────────
    // R14: FRN = (Capitaux Propres + Dettes LT) - Actif Immobilisé, as % of CA
    private double ratioFRN(FinancialDataDTO f, boolean p) {
        if (!pos(f.getRevenue()))
            return p ? 5.0 : 0.0;
        double frn = (bd(f.getEquity()) + bd(f.getLongTermDebt())) - bd(f.getFixedAssets());
        return lerp(frn / bd(f.getRevenue()), FRN_MIN, FRN_MAX, false);
    }

    // R15: BFR = Actif Circulant - Passif Circulant, as % of CA (inverted)
    private double ratioBFR(FinancialDataDTO f, boolean p) {
        if (!pos(f.getRevenue()))
            return p ? 5.0 : 0.0;
        double bfr = bd(f.getCurrentAssets()) - bd(f.getCurrentLiabilities());
        return lerp(bfr / bd(f.getRevenue()), BFR_MIN, BFR_MAX, true);
    }

    // 2. COMPORTEMENT PAIEMENT (0-100, weight=35%)

    private double calculatePaymentScore(FinancialDataDTO f) {
        if (f.getTotalPayments() == null || f.getTotalPayments() == 0) {
            log.debug("CAS-05: No payment history — neutral score 50");
            return 50.0;
        }
        double base = ((double) safe(f.getOnTimePayments()) / f.getTotalPayments()) * 100.0;
        double retardPenalty = safe(f.getLatePayments()) * 2.0;
        if (f.getAveragePaymentDelay() != null && f.getAveragePaymentDelay() > 30) {
            retardPenalty += (f.getAveragePaymentDelay() - 30) * 0.3;
        }
        double impayesPenalty = safe(f.getUnpaidCount()) * 10.0;
        double litigationPenalty = safe(f.getLitigationCount()) * 15.0;
        return Math.min(100, Math.max(0, base - retardPenalty - impayesPenalty - litigationPenalty));
    }

    // 3. SCORE CONTEXTE (0-100, weight=25%)

    private double calculateContextScore(CompanyDTO company, FinancialDataDTO f) {
        double base = 50.0;
        base += getSectorAdj(company.getIndustrySector());
        base += getAgeAdj(company.getIncorporationDate());
        base += getSizeBonus(f.getEmployeeCount());
        base += getCapitalBonus(f.getShareCapital());
        return Math.min(100, Math.max(0, base));
    }

    private int getSectorAdj(String sector) {
        if (sector == null)
            return 0;
        String up = sector.toUpperCase().trim();
        for (Map.Entry<String, Integer> e : SECTOR_ADJUSTMENTS.entrySet()) {
            if (up.contains(e.getKey()))
                return e.getValue();
        }
        return 0;
    }

    private int getAgeAdj(LocalDate inception) {
        if (inception == null)
            return 0;
        int years = Period.between(inception, LocalDate.now()).getYears();
        if (years >= 10)
            return 5;
        if (years >= 5)
            return 0;
        if (years >= 2)
            return -5;
        if (years >= 1)
            return -10;
        return -15;
    }

    private int getSizeBonus(Integer emp) {
        if (emp == null)
            return 0;
        if (emp >= 500)
            return 10;
        if (emp >= 100)
            return 7;
        if (emp >= 50)
            return 4;
        if (emp >= 10)
            return 2;
        return 0;
    }

    private int getCapitalBonus(BigDecimal capital) {
        if (capital == null)
            return 0;
        double v = capital.doubleValue();
        if (v >= 5_000_000)
            return 5;
        if (v >= 1_000_000)
            return 3;
        if (v >= 100_000)
            return 1;
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
        if (c == null || c.getIncorporationDate() == null)
            return false;
        return Period.between(c.getIncorporationDate(), LocalDate.now()).getYears() < 2;
    }

    private int countComputedRatios(FinancialDataDTO f) {
        int n = 0;
        if (pos(f.getCurrentAssets()) && pos(f.getCurrentLiabilities()))
            n++; // R1 LG
        if (pos(f.getCurrentLiabilities()))
            n++; // R2 LR
        if (pos(f.getCurrentLiabilities()))
            n++; // R3 LI
        if (pos(f.getTotalLiabilities()))
            n++; // R4 AF
        if (pos(f.getEquity()))
            n++; // R5 CE
        if (f.getOperatingIncome() != null)
            n++; // R6 CI
        if (pos(f.getTotalAssets()) && f.getNetResult() != null)
            n++; // R7 ROA
        if (pos(f.getRevenue()) && f.getNetResult() != null)
            n++; // R8 MN
        if (pos(f.getEquity()) && f.getNetResult() != null)
            n++; // R9 ROE
        if (pos(f.getRevenue()) && f.getEbitda() != null)
            n++; // R10 EBITDA
        if (pos(f.getRevenue()) && f.getAccountsReceivable() != null)
            n++; // R11 DSO
        if (f.getAccountsPayable() != null)
            n++; // R12 DF
        if (f.getInventory() != null)
            n++; // R13 RS
        if (pos(f.getRevenue()))
            n++; // R14 FRN
        if (pos(f.getRevenue()))
            n++; // R15 BFR
        return n;
    }

    // MATH UTILITIES

    // Linear interpolation: maps value to 0-10 note. Inverted = lower value →
    // higher note.
    private double lerp(double value, double min, double max, boolean inverted) {
        if (max == min)
            return 5.0;
        double ratio = (Math.min(max, Math.max(min, value)) - min) / (max - min);
        return Math.min(10, Math.max(0, inverted ? (1 - ratio) * 10.0 : ratio * 10.0));
    }

    private double avg(double... notes) {
        double sum = 0;
        for (double n : notes)
            sum += n;
        return sum / notes.length;
    }

    private double bd(BigDecimal v) {
        return v == null ? 0.0 : v.doubleValue();
    }

    private boolean pos(BigDecimal v) {
        return v != null && v.compareTo(ZERO) > 0;
    }

    private int safe(Integer v) {
        return v == null ? 0 : v;
    }
}
