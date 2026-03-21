package com.riskassessment.company.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Full financial data DTO — maps directly to FinancialData entity.
 * Contains all fields required for the 15 CDC financial ratios.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialDataDto {
    private Long id;
    private Long companyId;
    @NotNull(message = "Fiscal Year is required")
    private Integer fiscalYear;
    private String periodEndDate;

    // ── COMPTE DE RÉSULTAT ────────────────────────────────────────────────────
    private BigDecimal revenue; // CA TTC
    private BigDecimal netResult; // Résultat Net (alias netIncome)
    private BigDecimal operatingIncome; // Résultat d'Exploitation (EBIT)
    private BigDecimal financialExpenses; // Charges Financières
    private BigDecimal costOfGoodsSold; // Achats TTC (R12: Délai Fournisseurs)
    private BigDecimal ebitda;
    private BigDecimal tax;
    private BigDecimal depreciation;
    private BigDecimal amortization;

    // ── BILAN ACTIF ───────────────────────────────────────────────────────────
    private BigDecimal totalAssets; // Total Actif
    private BigDecimal currentAssets; // Actif Circulant
    private BigDecimal fixedAssets; // Actif Immobilisé
    private BigDecimal inventory; // Stocks
    private BigDecimal accountsReceivable; // Créances Clients
    private BigDecimal cash; // Trésorerie

    // ── BILAN PASSIF ──────────────────────────────────────────────────────────
    private BigDecimal equity; // Capitaux Propres
    private BigDecimal longTermDebt; // Dettes Long Terme
    private BigDecimal currentLiabilities; // Passif Circulant
    private BigDecimal accountsPayable; // Dettes Fournisseurs
    private BigDecimal totalLiabilities; // Total Passif

    // ── COMPORTEMENT DE PAIEMENT — CDC F-02.04 ───────────────────────────────
    private Integer paymentIncidents; // Incidents de paiement
    private Integer averagePaymentDelay; // Délai moyen de retard (jours)
    private Integer totalPayments; // Total paiements 12 mois
    private Integer onTimePayments; // Paiements à temps
    private Integer latePayments; // Paiements en retard
    private Integer unpaidCount; // Impayés existants
    private Integer litigationCount; // Contentieux en cours

    // ── CONTEXTE ─────────────────────────────────────────────────────────────
    private BigDecimal shareCapital; // Capital social
    private Integer employeeCount; // Pour bonus taille
}
