package com.riskassessment.scoring.dto;

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
    private Integer fiscalYear;
    private String periodEndDate;

    // ── BILAN ACTIF ──────────────────────────────────────────────────────────
    private BigDecimal totalAssets; // Total Actif
    private BigDecimal currentAssets; // Actif Circulant
    private BigDecimal fixedAssets; // Actif Immobilisé
    private BigDecimal inventory; // Stocks
    private BigDecimal accountsReceivable; // Créances Clients
    private BigDecimal cash; // Trésorerie

    // ── BILAN PASSIF ─────────────────────────────────────────────────────────
    private BigDecimal totalLiabilities; // Total Passif
    private BigDecimal currentLiabilities; // Passif Circulant
    private BigDecimal longTermDebt; // Dettes Long Terme
    private BigDecimal accountsPayable; // Dettes Fournisseurs
    private BigDecimal equity; // Capitaux Propres

    // ── COMPTE DE RÉSULTAT ───────────────────────────────────────────────────
    private BigDecimal revenue; // Chiffre d'Affaires (CA TTC)
    private BigDecimal netResult; // Résultat Net (alias: netIncome)
    private BigDecimal operatingIncome; // Résultat d'Exploitation (EBIT)
    private BigDecimal financialExpenses; // Charges Financières
    private BigDecimal ebitda; // EBITDA
    private BigDecimal costOfGoodsSold; // Coût des Marchandises Vendues

    // ── COMPORTEMENT DE PAIEMENT ─────────────────────────────────────────────
    private Integer totalPayments; // Total paiements 12 mois
    private Integer onTimePayments; // Paiements à temps
    private Integer latePayments; // Paiements en retard
    private Integer averagePaymentDelay; // Délai moyen de retard (jours)
    private Integer unpaidCount; // Nombre d'impayés
    private Integer litigationCount; // Contentieux en cours

    // ── DONNÉES CONTEXTUELLES ─────────────────────────────────────────────────
    private BigDecimal shareCapital; // Capital Social
    private Integer employeeCount; // Nombre d'employés
}
