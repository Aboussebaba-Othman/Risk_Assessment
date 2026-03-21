package com.riskassessment.analysis.service;

import com.riskassessment.analysis.entity.FinancialAnalysis;

import java.math.BigDecimal;
import java.util.List;

public interface SwotAnalysisService {

    FinancialAnalysis getLatestAnalysis(Long companyId);

    List<FinancialAnalysis> getAnalysisHistory(Long companyId);

    FinancialAnalysis performSwotAnalysis(Long companyId);

    FinancialAnalysis performSwotAnalysis(Long companyId, BigDecimal scoreValue, String riskLevel);
}
