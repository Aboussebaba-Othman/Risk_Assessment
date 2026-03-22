package com.riskassessment.scoring.strategy.scoring;

import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProfitabilityScorerTest {

    private ProfitabilityScorer scorer;

    @BeforeEach
    void setUp() {
        scorer = new ProfitabilityScorer();
    }

    @Test
    void testRatioROA_Normal() {
        // R7: Resultat Net / Total Actif
        FinancialDataDTO f = FinancialDataDTO.builder()
                .netResult(BigDecimal.valueOf(100))
                .totalAssets(BigDecimal.valueOf(1000))
                .build();
        // 100 / 1000 = 0.10. Scale [-0.05, 0.20]. Lerp: (0.10 - -0.05)/0.25 = 0.15/0.25 = 0.6 => 6.0
        assertEquals(6.0, scorer.ratioROA(f, false), 0.01);
    }

    @Test
    void testRatioROA_ZeroDenominator() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .netResult(BigDecimal.valueOf(100))
                .totalAssets(BigDecimal.ZERO)
                .build();
        assertEquals(0.0, scorer.ratioROA(f, false));
    }

    @Test
    void testRatioMN_Normal() {
        // R8: Resultat Net / Chiffre d'Affaires
        FinancialDataDTO f = FinancialDataDTO.builder()
                .netResult(BigDecimal.valueOf(50))
                .revenue(BigDecimal.valueOf(1000))
                .build();
        // 50 / 1000 = 0.05. Scale [-0.05, 0.20]. Lerp: (0.05 - -0.05)/0.25 = 0.10/0.25 = 0.4 => 4.0
        assertEquals(4.0, scorer.ratioMN(f, false), 0.01);
    }

    @Test
    void testRatioROE_Normal() {
        // R9: Resultat Net / Capitaux Propres
        FinancialDataDTO f = FinancialDataDTO.builder()
                .netResult(BigDecimal.valueOf(60))
                .equity(BigDecimal.valueOf(300))
                .build();
        // 60 / 300 = 0.20. Scale [-0.10, 0.30]. Lerp: (0.20 - -0.10)/0.40 = 0.30/0.40 = 0.75 => 7.5
        assertEquals(7.5, scorer.ratioROE(f, false), 0.01);
    }

    @Test
    void testRatioEBITDA_Normal() {
        // R10: EBITDA / Chiffre d'Affaires
        FinancialDataDTO f = FinancialDataDTO.builder()
                .ebitda(BigDecimal.valueOf(150))
                .revenue(BigDecimal.valueOf(1000))
                .build();
        // 150 / 1000 = 0.15. Scale [0.0, 0.30]. Lerp: (0.15 - 0)/0.30 = 0.5 => 5.0
        assertEquals(5.0, scorer.ratioEBITDA(f, false), 0.01);
    }

    @Test
    void testScore() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .netResult(BigDecimal.valueOf(100))
                .totalAssets(BigDecimal.valueOf(1000)) // ROA: 0.10 -> 6.0
                .revenue(BigDecimal.valueOf(1000)) // MN: 0.10 -> 6.0 | EBITDA: 150/1000=0.15 -> 5.0
                .equity(BigDecimal.valueOf(500)) // ROE: 100/500=0.20 -> 7.5
                .ebitda(BigDecimal.valueOf(150))
                .build();
        
        double expected = (6.0 + 6.0 + 7.5 + 5.0) / 4.0; // 24.5 / 4 = 6.125
        assertEquals(expected, scorer.score(f, false), 0.01);
    }
}
