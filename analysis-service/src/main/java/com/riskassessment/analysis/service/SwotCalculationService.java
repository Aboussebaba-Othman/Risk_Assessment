package com.riskassessment.analysis.service;

import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.enums.ResultType;
import com.riskassessment.analysis.enums.Severity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SwotCalculationService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;
    private final FinancialAnalysisService financialAnalysisService;

    public List<AnalysisResult> generateSwotResults(
            FinancialAnalysis analysis, 
            CompanyDTO company, 
            FinancialDataDTO fin, 
            BigDecimal score) {
            
        List<AnalysisResult> results = new ArrayList<>();
        results.addAll(deriveStrengths(analysis, company, fin, score));
        results.addAll(deriveWeaknesses(analysis, company, fin));
        results.addAll(deriveOpportunities(analysis, company, fin, score));
        results.addAll(deriveThreats(analysis, company, fin));
        
        return results;
    }

    // STRENGTHS
    private List<AnalysisResult> deriveStrengths(FinancialAnalysis analysis, CompanyDTO company, FinancialDataDTO fin, BigDecimal score) {
        List<AnalysisResult> strengths = new ArrayList<>();
        
        if (fin == null) {
            strengths.add(createResult(analysis, ResultType.STRENGTH, "Fondamentaux", "Structure OK", "Base existante.", Severity.LOW, "Consolider base."));
            return strengths;
        }

        if (hasSolidProfitability(fin)) {
            strengths.add(createResult(analysis, ResultType.STRENGTH, "Profitabilité", "Marge nette solide", "Marge > 10%.", Severity.LOW, "Maintenir la discipline."));
        }

        if (hasHighLiquidity(fin)) {
            strengths.add(createResult(analysis, ResultType.STRENGTH, "Liquidité", "Ratio élevé", "CR > 1.5", Severity.LOW, "Optimiser trésorerie."));
        }

        if (hasLowDebtLeverage(fin)) {
            strengths.add(createResult(analysis, ResultType.STRENGTH, "Solvabilité", "Dette faible", "Levier < 0.5", Severity.LOW, "Capacité financement externe."));
        }

        if (strengths.isEmpty()) {
            strengths.add(createResult(analysis, ResultType.STRENGTH, "Fondamentaux", "Structure OK", "Base existante.", Severity.LOW, "Consolider base."));
        }

        return strengths;
    }

    private boolean hasSolidProfitability(FinancialDataDTO fin) {
        if (fin.getRevenue() == null || fin.getNetResult() == null || fin.getRevenue().compareTo(ZERO) <= 0) {
            return false;
        }
        BigDecimal netProfitMargin = fin.getNetResult().divide(fin.getRevenue(), 4, RoundingMode.HALF_UP);
        return netProfitMargin.compareTo(new BigDecimal("0.10")) >= 0;
    }

    private boolean hasHighLiquidity(FinancialDataDTO fin) {
        if (fin.getCurrentAssets() == null || fin.getCurrentLiabilities() == null || fin.getCurrentLiabilities().compareTo(ZERO) <= 0) {
            return false;
        }
        BigDecimal currentRatio = fin.getCurrentAssets().divide(fin.getCurrentLiabilities(), 4, RoundingMode.HALF_UP);
        return currentRatio.compareTo(new BigDecimal("1.5")) >= 0;
    }

    private boolean hasLowDebtLeverage(FinancialDataDTO fin) {
        if (fin.getEquity() == null || fin.getEquity().compareTo(ZERO) <= 0) {
            return false;
        }
        BigDecimal totalDebt = financialAnalysisService.safeAdd(fin.getLongTermDebt(), fin.getCurrentLiabilities());
        BigDecimal debtToEquity = totalDebt.divide(fin.getEquity(), 4, RoundingMode.HALF_UP);
        return debtToEquity.compareTo(new BigDecimal("0.5")) < 0;
    }

    // WEAKNESSES
    private List<AnalysisResult> deriveWeaknesses(FinancialAnalysis analysis, CompanyDTO company, FinancialDataDTO fin) {
        List<AnalysisResult> weaknesses = new ArrayList<>();
        
        if (fin == null) {
            weaknesses.add(createResult(analysis, ResultType.WEAKNESS, "Analyse", "Données manquantes", "Incomplet", Severity.LOW, "Compléter ERP."));
            return weaknesses;
        }

        if (hasNegativeEquity(fin)) {
            weaknesses.add(createResult(analysis, ResultType.WEAKNESS, "Solvabilité", "Capitaux négatifs", "Insolvabilité critique", Severity.CRITICAL, "Restructuration urgente."));
        }

        if (hasLowLiquidity(fin)) {
            weaknesses.add(createResult(analysis, ResultType.WEAKNESS, "Liquidité", "Insuffisance", "CR < 1", Severity.HIGH, "Gérer le BFR."));
        }

        if (hasPaymentIncidents(fin)) {
            weaknesses.add(createResult(analysis, ResultType.WEAKNESS, "Paiement", "Incidents", "Retards constatés", Severity.HIGH, "Régulariser paiements."));
        }

        if (weaknesses.isEmpty()) {
            weaknesses.add(createResult(analysis, ResultType.WEAKNESS, "Analyse", "Données incomplètes", "Risques non détectés", Severity.LOW, "Vérification manuelle."));
        }

        return weaknesses;
    }

    private boolean hasNegativeEquity(FinancialDataDTO fin) {
        return fin.getEquity() != null && fin.getEquity().compareTo(ZERO) < 0;
    }

    private boolean hasLowLiquidity(FinancialDataDTO fin) {
        if (fin.getCurrentAssets() == null || fin.getCurrentLiabilities() == null || fin.getCurrentLiabilities().compareTo(ZERO) <= 0) {
            return false;
        }
        BigDecimal currentRatio = fin.getCurrentAssets().divide(fin.getCurrentLiabilities(), 4, RoundingMode.HALF_UP);
        return currentRatio.compareTo(BigDecimal.ONE) < 0;
    }

    private boolean hasPaymentIncidents(FinancialDataDTO fin) {
        return fin.getPaymentIncidents() != null && fin.getPaymentIncidents() > 0;
    }

    // OPPORTUNITIES
    private List<AnalysisResult> deriveOpportunities(FinancialAnalysis analysis, CompanyDTO company, FinancialDataDTO fin, BigDecimal score) {
        List<AnalysisResult> opportunities = new ArrayList<>();

        if (isInDynamicTechSector(company)) {
            opportunities.add(createResult(analysis, ResultType.OPPORTUNITY, "Secteur", "Marché dynamique", "Secteur technologique en croissance", Severity.LOW, "Investir R&D."));
        }

        if (hasModerateScorePotential(score)) {
            opportunities.add(createResult(analysis, ResultType.OPPORTUNITY, "Score", "Amélioration cible", "Potentiel BBB", Severity.MEDIUM, "Réduire délais d'encaissement."));
        }

        if (opportunities.isEmpty()) {
            opportunities.add(createResult(analysis, ResultType.OPPORTUNITY, "Marché", "Optimisation structurale", "Process inefficaces", Severity.LOW, "Automatiser."));
        }

        return opportunities;
    }

    private boolean isInDynamicTechSector(CompanyDTO company) {
        if (company == null || company.getIndustrySector() == null) {
            return false;
        }
        String sector = company.getIndustrySector().toLowerCase();
        return sector.contains("tech") || sector.contains("software") || sector.contains("it");
    }

    private boolean hasModerateScorePotential(BigDecimal score) {
        if (score == null) return false;
        return score.compareTo(new BigDecimal("40")) >= 0 && score.compareTo(new BigDecimal("70")) < 0;
    }

    // THREATS
    private List<AnalysisResult> deriveThreats(FinancialAnalysis analysis, CompanyDTO company, FinancialDataDTO fin) {
        List<AnalysisResult> threats = new ArrayList<>();

        if (hasLongPaymentDelays(fin)) {
            threats.add(createResult(analysis, ResultType.THREAT, "Fournisseurs", "Risque rupture", "Délais de paiement moyens > 60 jours", Severity.HIGH, "Prioriser paiements critiques."));
        }

        if (isInVulnerableSector(company)) {
            threats.add(createResult(analysis, ResultType.THREAT, "Macroéconomie", "Secteur cyclique", "Vulnérabilité aux taux d'intérêt", Severity.MEDIUM, "Diversifier portefeuille."));
        }

        if (threats.isEmpty()) {
            threats.add(createResult(analysis, ResultType.THREAT, "Marché", "Pression concurrente", "Marges en baisse macroéconomique", Severity.LOW, "Surveiller les concurrents directs."));
        }

        return threats;
    }

    private boolean hasLongPaymentDelays(FinancialDataDTO fin) {
        return fin != null && fin.getAveragePaymentDelay() != null && fin.getAveragePaymentDelay() > 60;
    }

    private boolean isInVulnerableSector(CompanyDTO company) {
        if (company == null || company.getIndustrySector() == null) {
            return false;
        }
        String sector = company.getIndustrySector().toLowerCase();
        return sector.contains("construction") || sector.contains("immobilier");
    }
    // UTILS
    private AnalysisResult createResult(
            FinancialAnalysis analysis, 
            ResultType type, 
            String category, 
            String title, 
            String description, 
            Severity severity, 
            String recommendation) {
            
        AnalysisResult result = new AnalysisResult();
        result.setAnalysis(analysis);
        result.setResultType(type);
        result.setCategory(category);
        result.setTitle(title);
        result.setDescription(description);
        result.setSeverity(severity);
        result.setRecommendation(recommendation);
        
        return result;
    }
}
