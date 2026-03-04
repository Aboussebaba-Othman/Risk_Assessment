package com.riskassessment.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataDto {
    private Long id;
    private Long companyId;
    private Integer fiscalYear;
    private String periodEndDate;

    // Revenue / Income
    private BigDecimal revenue;
    private BigDecimal netResult;
    private BigDecimal operatingIncome;
    private BigDecimal financialExpenses;
    private BigDecimal tax;
    private BigDecimal ebitda;
    private BigDecimal depreciation;
    private BigDecimal amortization;

    // Balance Sheet
    private BigDecimal equity;
    private BigDecimal longTermDebt;
    private BigDecimal currentAssets;
    private BigDecimal currentLiabilities;
    private BigDecimal inventory;
    private BigDecimal accountsReceivable;
    private BigDecimal accountsPayable;
    private BigDecimal cash;
    private BigDecimal totalAssets;
    private BigDecimal totalLiabilities;
    private BigDecimal fixedAssets;

    // Payment behavior
    private Integer paymentIncidents;
    private Integer averagePaymentDelay;
}
