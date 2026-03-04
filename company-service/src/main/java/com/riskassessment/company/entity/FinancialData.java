package com.riskassessment.company.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Financial data entity for storing company financial statements
 * Used for risk scoring and analysis
 */
@Entity
@Table(name = "financial_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    @Column(name = "period_end_date", nullable = false)
    private LocalDate periodEndDate;

    // ACTIF (Assets)
    @Column(name = "total_assets", precision = 15, scale = 2)
    private BigDecimal totalAssets;

    @Column(name = "current_assets", precision = 15, scale = 2)
    private BigDecimal currentAssets;

    @Column(name = "fixed_assets", precision = 15, scale = 2)
    private BigDecimal fixedAssets;

    @Column(precision = 15, scale = 2)
    private BigDecimal inventory;

    @Column(name = "accounts_receivable", precision = 15, scale = 2)
    private BigDecimal accountsReceivable;

    @Column(precision = 15, scale = 2)
    private BigDecimal cash;

    // PASSIF (Liabilities)
    @Column(name = "total_liabilities", precision = 15, scale = 2)
    private BigDecimal totalLiabilities;

    @Column(name = "current_liabilities", precision = 15, scale = 2)
    private BigDecimal currentLiabilities;

    @Column(name = "long_term_debt", precision = 15, scale = 2)
    private BigDecimal longTermDebt;

    @Column(name = "accounts_payable", precision = 15, scale = 2)
    private BigDecimal accountsPayable;

    @Column(precision = 15, scale = 2)
    private BigDecimal equity;

    // COMPTE DE RÉSULTAT (Income Statement)
    @Column(precision = 15, scale = 2)
    private BigDecimal revenue;

    @Column(name = "cost_of_goods_sold", precision = 15, scale = 2)
    private BigDecimal costOfGoodsSold;

    @Column(name = "operating_expenses", precision = 15, scale = 2)
    private BigDecimal operatingExpenses;

    @Column(name = "operating_income", precision = 15, scale = 2)
    private BigDecimal operatingIncome;

    @Column(name = "financial_expenses", precision = 15, scale = 2)
    private BigDecimal financialExpenses;

    @Column(name = "net_income", precision = 15, scale = 2)
    private BigDecimal netIncome;

    @Column(precision = 15, scale = 2)
    private BigDecimal ebitda;

    // RATIOS CALCULÉS (Calculated Ratios) - Optionnel, peuvent être calculés à la
    // volée
    @Column(name = "working_capital", precision = 15, scale = 2)
    private BigDecimal workingCapital;

    @Column(name = "working_capital_requirement", precision = 15, scale = 2)
    private BigDecimal workingCapitalRequirement;

    // AUDIT
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
