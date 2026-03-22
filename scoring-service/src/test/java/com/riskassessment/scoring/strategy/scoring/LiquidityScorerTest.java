package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LiquidityScorerTest {

    private LiquidityScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new LiquidityScorer();
    }

    @Test
    void testRatioLG_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .currentAssets(BigDecimal.valueOf(2000))
                .currentLiabilities(BigDecimal.valueOf(1000))
                .build();
        assertEquals(6.0, scorer.ratioLG(f, false), 0.01);
    }

    @Test
    void testRatioLG_ZeroDenominator() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .currentAssets(BigDecimal.valueOf(2000))
                .currentLiabilities(BigDecimal.ZERO)
                .build();
        assertEquals(0.0, scorer.ratioLG(f, false));
        assertEquals(5.0, scorer.ratioLG(f, true));
    }

    @Test
    void testRatioLR_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .currentAssets(BigDecimal.valueOf(2000))
                .inventory(BigDecimal.valueOf(500))
                .currentLiabilities(BigDecimal.valueOf(1000))
                .build();
        assertEquals(7.058, scorer.ratioLR(f, false), 0.01);
    }

    @Test
    void testRatioLI_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .cash(BigDecimal.valueOf(500))
                .currentLiabilities(BigDecimal.valueOf(1000))
                .build();
        assertEquals(5.0, scorer.ratioLI(f, false), 0.01);
    }

    @Test
    void testScore_AveragesCorrectly() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .currentAssets(BigDecimal.valueOf(1500)) 
                .inventory(BigDecimal.valueOf(500)) 
                .cash(BigDecimal.valueOf(250)) 
                .currentLiabilities(BigDecimal.valueOf(1000))
                .build();
        
        double expectedAvg = (4.0 + 4.117 + 2.5) / 3.0; 
        assertEquals(expectedAvg, scorer.score(f, false), 0.01);
    }
}
