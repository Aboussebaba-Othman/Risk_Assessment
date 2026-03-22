package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ManagementScorerTest {

    private ManagementScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new ManagementScorer();
    }

    @Test
    void testRatioDSO_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .accountsReceivable(BigDecimal.valueOf(250))
                .revenue(BigDecimal.valueOf(1000))
                .build();
        assertEquals(6.0, scorer.ratioDSO(f, false), 0.01);
    }

    @Test
    void testRatioDF_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .accountsPayable(BigDecimal.valueOf(150))
                .costOfGoodsSold(BigDecimal.valueOf(600))
                .build();
        assertEquals(7.14, scorer.ratioDF(f, false), 0.01);
    }

    @Test
    void testRatioDF_UsesRevenueWhenCOGSIsZero() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .accountsPayable(BigDecimal.valueOf(150))
                .costOfGoodsSold(BigDecimal.ZERO)
                .revenue(BigDecimal.valueOf(1000)) 
                .build();
        assertEquals(7.14, scorer.ratioDF(f, false), 0.01);
    }

    @Test
    void testRatioRS_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .revenue(BigDecimal.valueOf(1000))
                .inventory(BigDecimal.valueOf(200))
                .build();
        assertEquals(4.16, scorer.ratioRS(f, false), 0.01);
    }

    @Test
    void testRatioRS_ZeroInventoryIsServiceSector() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .revenue(BigDecimal.valueOf(1000))
                .inventory(BigDecimal.ZERO)
                .build();
        assertEquals(5.0, scorer.ratioRS(f, false));
    }

    @Test
    void testRatioFRN_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .equity(BigDecimal.valueOf(500))
                .longTermDebt(BigDecimal.valueOf(200))
                .fixedAssets(BigDecimal.valueOf(400))
                .revenue(BigDecimal.valueOf(1000))
                .build();
        assertEquals(10.0, scorer.ratioFRN(f, false), 0.01);
    }

    @Test
    void testRatioBFR_Normal() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .currentAssets(BigDecimal.valueOf(500))
                .currentLiabilities(BigDecimal.valueOf(300))
                .revenue(BigDecimal.valueOf(1000))
                .build();
        assertEquals(0.0, scorer.ratioBFR(f, false), 0.01);
    }

    @Test
    void testScores() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .accountsReceivable(BigDecimal.valueOf(250)) 
                .accountsPayable(BigDecimal.valueOf(150)) 
                .costOfGoodsSold(BigDecimal.valueOf(600))
                .revenue(BigDecimal.valueOf(1000))
                .inventory(BigDecimal.valueOf(200)) 
                .equity(BigDecimal.valueOf(500)) 
                .longTermDebt(BigDecimal.valueOf(200))
                .fixedAssets(BigDecimal.valueOf(400))
                .currentAssets(BigDecimal.valueOf(500)) 
                .currentLiabilities(BigDecimal.valueOf(300))
                .build();
        
        double gestExpected = (6.0 + 7.142 + 4.166) / 3.0; 
        assertEquals(gestExpected, scorer.gestScore(f, false), 0.01);

        double structExpected = (10.0 + 0.0) / 2.0; 
        assertEquals(structExpected, scorer.structureScore(f, false), 0.01);
    }
}
