package com.riskassessment.scoring.strategy;

import com.riskassessment.scoring.dto.CompanyDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import com.riskassessment.scoring.dto.ScoringResult;

public interface ScoringStrategy {

    ScoringResult calculate(CompanyDTO company, FinancialDataDTO financials);
}
