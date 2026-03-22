package com.riskassessment.scoring.strategy.impl;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.dto.ScoringResult;
import com.riskassessment.scoring.strategy.scoring.LiquidityScorer;
import com.riskassessment.scoring.strategy.scoring.ManagementScorer;
import com.riskassessment.scoring.strategy.scoring.ProfitabilityScorer;
import com.riskassessment.scoring.strategy.scoring.SolvencyScorer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandardScoringStrategyTest {

    @Mock
    private LiquidityScorer liquidityScorer;
    @Mock
    private SolvencyScorer solvencyScorer;
    @Mock
    private ProfitabilityScorer profitabilityScorer;
    @Mock
    private ManagementScorer managementScorer;

    @InjectMocks
    private StandardScoringStrategy strategy;

    private CompanyDTO defaultCompany;

    @BeforeEach
    void setUp() {
        defaultCompany = CompanyDTO.builder()
                .incorporationDate(LocalDate.now().minusYears(5)) 
                .industrySector("RETAIL") 
                .build();
    }

    @Test
    void testCAS06_CollectiveProcedure() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .litigationCount(3)
                .build(); 
        ScoringResult result = strategy.calculate(defaultCompany, f);
        assertEquals(0, result.getFinalScore());
        assertTrue(result.getScoringNotes().contains("CAS-06"));
    }

    @Test
    void testCAS04_NegativeEquity() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .equity(BigDecimal.valueOf(-100))
                .build();
        
        ScoringResult result = strategy.calculate(defaultCompany, f);
        assertEquals(24, result.getFinalScore());
        assertTrue(result.getScoringNotes().contains("CAS-04"));
    }

    @Test
    void testCAS02_InsufficientData() {
        FinancialDataDTO f = FinancialDataDTO.builder()
                .revenue(BigDecimal.valueOf(1000))
                .build();
        
        ScoringResult result = strategy.calculate(defaultCompany, f);
        assertEquals(0, result.getFinalScore());
        assertTrue(result.getScoringNotes().contains("CAS-02"));
    }

    @Test
    void testCAS01_YoungCompanyCapped() {
        CompanyDTO young = CompanyDTO.builder()
                .incorporationDate(LocalDate.now().minusYears(1))
                .industrySector("RETAIL")
                .build();
        
        FinancialDataDTO f = buildFullyPopulatedFinancials();
        mockScorersToReturnPerfectScore();
        
        ScoringResult result = strategy.calculate(young, f);
        assertEquals(65, result.getFinalScore()); // Capped at 65
        assertTrue(result.getScoringNotes().contains("CAS-01"));
    }

    @Test
    void testCAS03_NegativeNetResultPenalty() {
        FinancialDataDTO f = buildFullyPopulatedFinancials();
        f.setNetResult(BigDecimal.valueOf(-50)); 
        
        mockScorersToReturnPerfectScore();         
        ScoringResult result = strategy.calculate(defaultCompany, f);
        assertTrue(result.getScoringNotes().contains("CAS-03"));
    }

    @Test
    void testNormalScoring_WeightedAverage() {
        FinancialDataDTO f = buildFullyPopulatedFinancials();
        when(liquidityScorer.score(any(), anyBoolean())).thenReturn(5.0);
        when(solvencyScorer.score(any(), anyBoolean())).thenReturn(5.0);
        when(profitabilityScorer.score(any(), anyBoolean())).thenReturn(5.0);
        when(managementScorer.gestScore(any(), anyBoolean())).thenReturn(5.0);
        when(managementScorer.structureScore(any(), anyBoolean())).thenReturn(5.0);
        ScoringResult result = strategy.calculate(defaultCompany, f);
        assertTrue(result.getFinalScore() > 0 && result.getFinalScore() <= 100);
    }

    private void mockScorersToReturnPerfectScore() {
        when(liquidityScorer.score(any(), anyBoolean())).thenReturn(10.0);
        when(solvencyScorer.score(any(), anyBoolean())).thenReturn(10.0);
        when(profitabilityScorer.score(any(), anyBoolean())).thenReturn(10.0);
        when(managementScorer.gestScore(any(), anyBoolean())).thenReturn(10.0);
        when(managementScorer.structureScore(any(), anyBoolean())).thenReturn(10.0);
    }

    private FinancialDataDTO buildFullyPopulatedFinancials() {
        return FinancialDataDTO.builder()
                .totalAssets(BigDecimal.valueOf(1000))
                .currentAssets(BigDecimal.valueOf(500))
                .fixedAssets(BigDecimal.valueOf(500))
                .inventory(BigDecimal.valueOf(200))
                .cash(BigDecimal.valueOf(100))
                .totalLiabilities(BigDecimal.valueOf(1000))
                .currentLiabilities(BigDecimal.valueOf(400))
                .longTermDebt(BigDecimal.valueOf(200))
                .equity(BigDecimal.valueOf(400))
                .revenue(BigDecimal.valueOf(2000))
                .netResult(BigDecimal.valueOf(200))
                .operatingIncome(BigDecimal.valueOf(250))
                .build();
    }
}
