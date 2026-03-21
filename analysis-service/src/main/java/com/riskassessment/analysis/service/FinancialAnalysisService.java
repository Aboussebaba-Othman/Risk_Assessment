package com.riskassessment.analysis.service;

import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.enums.OverallHealth;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class FinancialAnalysisService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;


    public OverallHealth deriveHealth(FinancialDataDTO f, BigDecimal score) {
        if (score != null) {
            if (score.compareTo(new BigDecimal("70")) >= 0) return OverallHealth.GOOD;
            if (score.compareTo(new BigDecimal("40")) >= 0) return OverallHealth.MODERATE;
            return OverallHealth.POOR;
        }
        
        if (f == null) return OverallHealth.MODERATE;
        
        if (f.getNetResult() != null && f.getNetResult().compareTo(ZERO) > 0
                && f.getEquity() != null && f.getEquity().compareTo(ZERO) > 0) {
            return OverallHealth.GOOD;
        }
        if (f.getEquity() != null && f.getEquity().compareTo(ZERO) < 0) {
            return OverallHealth.POOR;
        }
        
        return OverallHealth.MODERATE;
    }


    public void populateFinancialSnapshot(FinancialAnalysis analysis, FinancialDataDTO fin) {
        if (fin == null) return;
        
        analysis.setRevenue(fin.getRevenue());
        analysis.setExpenses(fin.getTotalLiabilities());
        analysis.setNetProfit(fin.getNetResult());
        
        BigDecimal syntheticAssets = safeAdd(fin.getCurrentAssets(), safeAdd(fin.getFixedAssets(), fin.getEquity()));
        analysis.setAssets(fin.getTotalAssets() != null ? fin.getTotalAssets() : syntheticAssets);
        
        analysis.setLiabilities(safeAdd(fin.getCurrentLiabilities(), fin.getLongTermDebt()));
        analysis.setEquity(fin.getEquity());
        analysis.setCashFlow(fin.getCash());
        analysis.setCurrency("EUR");
    }

   
    public BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        return (a != null ? a : ZERO).add(b != null ? b : ZERO);
    }
}
