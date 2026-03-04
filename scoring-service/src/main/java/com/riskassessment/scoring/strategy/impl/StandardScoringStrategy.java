package com.riskassessment.scoring.strategy.impl;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.strategy.ScoringStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

@Component
public class StandardScoringStrategy implements ScoringStrategy {

    @Override
    public int calculate(CompanyDTO company, FinancialDataDTO financials) {
        if (financials == null) {
            return 20; 
        }

        int score = 0;

        // 1. Profitability (40 points max)
        score += calculateProfitabilityScore(financials);

        // 2. Solvency (30 points max)
        score += calculateSolvencyScore(financials);

        // 3. Stability / Age (30 points max)
        score += calculateStabilityScore(company);

        return Math.min(100, Math.max(0, score));
    }

    private int calculateProfitabilityScore(FinancialDataDTO f) {
        int points = 0;
        if (f.getRevenue() != null && f.getRevenue().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margin = f.getNetResult().divide(f.getRevenue(), 4, RoundingMode.HALF_UP);

            if (margin.compareTo(new BigDecimal("0.20")) > 0)
                points = 40; // > 20% margin
            else if (margin.compareTo(new BigDecimal("0.10")) > 0)
                points = 30;
            else if (margin.compareTo(new BigDecimal("0.00")) > 0)
                points = 20;
            else
                points = 0; // Negative margin
        }
        return points;
    }

    private int calculateSolvencyScore(FinancialDataDTO f) {
        if (f.getEquity() == null || f.getEquity().compareTo(BigDecimal.ZERO) == 0)
            return 0;

        BigDecimal totalDebt = (f.getLongTermDebt() != null ? f.getLongTermDebt() : BigDecimal.ZERO)
                .add(f.getCurrentLiabilities() != null ? f.getCurrentLiabilities() : BigDecimal.ZERO);

        BigDecimal debtToEquity = totalDebt.divide(f.getEquity(), 4, RoundingMode.HALF_UP);

        if (debtToEquity.compareTo(new BigDecimal("0.5")) < 0)
            return 30; // Very healthy
        else if (debtToEquity.compareTo(new BigDecimal("1.0")) < 0)
            return 20;
        else if (debtToEquity.compareTo(new BigDecimal("2.0")) < 0)
            return 10;

        return 0; 
    }

    private int calculateStabilityScore(CompanyDTO c) {
        if (c.getIncorporationDate() == null)
            return 0;

        int years = Period.between(c.getIncorporationDate(), LocalDate.now()).getYears();

        if (years > 10)
            return 30;
        else if (years > 5)
            return 20;
        else if (years > 2)
            return 10;

        return 0; 
    }
}
