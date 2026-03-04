package com.riskassessment.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDataDTO {
    private Long id;
    private Long companyId;
    private Integer fiscalYear;
    private BigDecimal totalRevenue;
    private BigDecimal grossProfit;
    private BigDecimal operatingIncome;
    private BigDecimal netIncome;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal currentAssets;
    private BigDecimal currentLiabilities;
    private BigDecimal equity;
    private LocalDate statementDate;
}
