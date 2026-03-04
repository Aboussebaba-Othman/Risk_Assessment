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
    private BigDecimal revenue;
    private BigDecimal netResult;
    private BigDecimal operatingIncome;
    private BigDecimal financialExpenses;
    private BigDecimal tax;
    private BigDecimal ebitda;
    private BigDecimal depreciation;
    private BigDecimal amortization;

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

    private Integer paymentIncidents;
    private Integer averagePaymentDelay;
}
