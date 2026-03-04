package com.riskassessment.scoring.strategy;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;

public interface ScoringStrategy {

    int calculate(CompanyDTO company, FinancialDataDTO financials);
}
