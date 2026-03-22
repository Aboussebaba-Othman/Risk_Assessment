package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SolvencyScorerTest {

    private SolvencyScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new SolvencyScorer();
    }

    @Test
    void testRatioAF_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .equity(BigDecimal.valueOf(350))
                .totalLiabilities(BigDecimal.valueOf(1000))
                .build();
        assertEquals(5.0, scorer.ratioAF(f, false), 0.01);
    }

    @Test
    void testRatioAF_ZeroDenominator() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .equity(BigDecimal.valueOf(350))
                .totalLiabilities(BigDecimal.ZERO)
                .build();
        assertEquals(0.0, scorer.ratioAF(f, false));
        assertEquals(5.0, scorer.ratioAF(f, true));
    }

    @Test
    void testRatioCE_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .longTermDebt(BigDecimal.valueOf(400))
                .currentLiabilities(BigDecimal.valueOf(100))
                .equity(BigDecimal.valueOf(500))
                .build();
        assertEquals(6.66, scorer.ratioCE(f, false), 0.01);
    }

    @Test
    void testRatioCE_ZeroDenominator() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .longTermDebt(BigDecimal.valueOf(400))
                .currentLiabilities(BigDecimal.valueOf(100))
                .equity(BigDecimal.ZERO)
                .build();
        assertEquals(0.0, scorer.ratioCE(f, false));
    }

    @Test
    void testRatioCI_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .operatingIncome(BigDecimal.valueOf(550))
                .financialExpenses(BigDecimal.valueOf(100))
                .build();
        assertEquals(5.0, scorer.ratioCI(f, false), 0.01);
    }

    @Test
    void testRatioCI_ZeroDenominator() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .operatingIncome(BigDecimal.valueOf(550))
                .financialExpenses(BigDecimal.ZERO)
                .build();
        assertEquals(5.0, scorer.ratioCI(f, false));
    }

    @Test
    void testScore() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .equity(BigDecimal.valueOf(350)) 
                .totalLiabilities(BigDecimal.valueOf(1000))
                .longTermDebt(BigDecimal.valueOf(200)) 
                .currentLiabilities(BigDecimal.valueOf(500))
                .operatingIncome(BigDecimal.valueOf(550)) 
                .financialExpenses(BigDecimal.valueOf(100))
                .build();
        
        double expected = (5.0 + 3.333 + 5.0) / 3.0; 
        assertEquals(expected, scorer.score(f, false), 0.01);
    }
}
