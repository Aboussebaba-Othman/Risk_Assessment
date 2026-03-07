package com.riskassessment.scoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO received from company-service /api/v1/companies/{id}/financials/latest.
 * Maps directly to the FinancialData entity in company-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyFinancialsDTO {

    // ACTIF
    private BigDecimal totalAssets;
    private BigDecimal currentAssets;
    private BigDecimal fixedAssets;
    private BigDecimal inventory;
    private BigDecimal accountsReceivable;
    private BigDecimal cash;

    // PASSIF
    private BigDecimal totalLiabilities;
    private BigDecimal currentLiabilities;
    private BigDecimal longTermDebt;
    private BigDecimal accountsPayable;
    private BigDecimal equity;

    // RÉSULTAT
    private BigDecimal revenue;
    private BigDecimal netResult;
    private BigDecimal operatingIncome;
    private BigDecimal financialExpenses;
    private BigDecimal ebitda;
    private BigDecimal costOfGoodsSold;

    // PAIEMENT
    private Integer totalPayments;
    private Integer onTimePayments;
    private Integer latePayments;
    private Integer averagePaymentDelay;
    private Integer unpaidCount;
    private Integer litigationCount;

    // CONTEXTE
    private BigDecimal shareCapital;
    private Integer employeeCount;
}
