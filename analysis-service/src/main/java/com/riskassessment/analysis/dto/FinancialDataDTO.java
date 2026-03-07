package com.riskassessment.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDataDTO {
    // Income Statement
    private BigDecimal revenue;
    private BigDecimal netResult;
    private BigDecimal operatingIncome;
    private BigDecimal financialExpenses;
    private BigDecimal costOfGoodsSold;
    private BigDecimal tax;
    private BigDecimal ebitda;
    private BigDecimal depreciation;
    private BigDecimal amortization;

    // Balance Sheet - Assets
    private BigDecimal totalAssets;
    private BigDecimal currentAssets;
    private BigDecimal fixedAssets;
    private BigDecimal inventory;
    private BigDecimal accountsReceivable;
    private BigDecimal cash;

    // Balance Sheet - Liabilities
    private BigDecimal equity;
    private BigDecimal longTermDebt;
    private BigDecimal currentLiabilities;
    private BigDecimal accountsPayable;
    private BigDecimal totalLiabilities;

    // Payment Behavior (CDC F-02.04)
    private Integer paymentIncidents;
    private Integer averagePaymentDelay;
    private Integer totalPayments;
    private Integer onTimePayments;
    private Integer latePayments;
    private Integer unpaidCount;
    private Integer litigationCount;

    // Context
    private BigDecimal shareCapital;
    private Integer employeeCount;
}
