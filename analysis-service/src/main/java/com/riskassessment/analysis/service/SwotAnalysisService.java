package com.riskassessment.analysis.service;

import com.riskassessment.analysis.client.CompanyClient;
import com.riskassessment.analysis.client.FinancialsClient;
import com.riskassessment.analysis.dto.CompanyDTO;
import com.riskassessment.analysis.dto.FinancialDataDTO;
import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.entity.enums.*;
import com.riskassessment.analysis.repository.FinancialAnalysisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SwotAnalysisService {

    private final CompanyClient companyClient;
    private final FinancialsClient financialsClient;
    private final FinancialAnalysisRepository repository;

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    @Transactional
    public FinancialAnalysis performSwotAnalysis(Long companyId) {
        return performSwotAnalysis(companyId, null, null);
    }

    @Transactional
    public FinancialAnalysis performSwotAnalysis(Long companyId, BigDecimal scoreValue, String riskLevel) {
        log.info("Starting SWOT analysis for companyId={}", companyId);

        // ── 1. Fetch real data ─────────────────────────────────────────────
        CompanyDTO company = fetchCompany(companyId);
        FinancialDataDTO fin = fetchFinancials(companyId);

        // ── 2. Build the parent FinancialAnalysis record ───────────────────
        OverallHealth health = deriveHealth(fin, scoreValue);

        FinancialAnalysis analysis = new FinancialAnalysis();
        analysis.setCompanyId(companyId);
        analysis.setTenantId(1L);
        analysis.setAnalysisType(AnalysisType.SWOT);
        analysis.setPeriodStart(LocalDate.now().minusMonths(12));
        analysis.setPeriodEnd(LocalDate.now());
        analysis.setStatus(AnalysisStatus.COMPLETED);
        analysis.setOverallHealth(health);

        // Populate financial snapshot from real data
        if (fin != null) {
            analysis.setRevenue(fin.getRevenue());
            analysis.setExpenses(fin.getTotalLiabilities());
            analysis.setNetProfit(fin.getNetResult());
            analysis.setAssets(fin.getTotalAssets() != null ? fin.getTotalAssets()
                    : safeAdd(fin.getCurrentAssets(), safeAdd(fin.getFixedAssets(), fin.getEquity())));
            analysis.setLiabilities(safeAdd(fin.getCurrentLiabilities(), fin.getLongTermDebt()));
            analysis.setEquity(fin.getEquity());
            analysis.setCashFlow(fin.getCash());
        }
        analysis.setCurrency("EUR");

        String scoreNote = scoreValue != null
                ? String.format(" | Risk Score: %.0f/100 (%s)", scoreValue.doubleValue(), riskLevel)
                : "";
        analysis.setNotes("Automated SWOT — " + LocalDate.now() + scoreNote);

        // ── 3. Generate SWOT results from real data ────────────────────────
        List<AnalysisResult> results = generateSwotResults(analysis, company, fin, scoreValue);
        analysis.setResults(results);

        FinancialAnalysis saved = repository.save(analysis);
        log.info("SWOT analysis saved id={} companyId={} health={} results={}",
                saved.getId(), companyId, health, results.size());
        return saved;
    }

   
    private List<AnalysisResult> generateSwotResults(
            FinancialAnalysis analysis,
            CompanyDTO company,
            FinancialDataDTO fin,
            BigDecimal scoreValue) {

        List<AnalysisResult> results = new ArrayList<>();

        results.addAll(generateStrengths(analysis, company, fin, scoreValue));
        results.addAll(generateWeaknesses(analysis, company, fin));
        results.addAll(generateOpportunities(analysis, company, fin, scoreValue));
        results.addAll(generateThreats(analysis, company, fin));

        return results;
    }

    // ── STRENGTHS ────────────────────────────────────────────────────────────

    private List<AnalysisResult> generateStrengths(
            FinancialAnalysis analysis, CompanyDTO c, FinancialDataDTO f, BigDecimal score) {
        List<AnalysisResult> list = new ArrayList<>();

        // Strong profitability
        if (f != null && f.getRevenue() != null && f.getNetResult() != null
                && f.getRevenue().compareTo(ZERO) > 0) {
            BigDecimal npm = f.getNetResult().divide(f.getRevenue(), 4, RoundingMode.HALF_UP);
            if (npm.compareTo(new BigDecimal("0.10")) >= 0) {
                list.add(result(analysis, ResultType.STRENGTH, "Profitabilité",
                        "Marge nette solide",
                        String.format("Marge nette de %.1f%% — au-dessus du seuil de 10%% attendu.",
                                npm.multiply(BigDecimal.valueOf(100)).doubleValue()),
                        Severity.LOW,
                        "Maintenir la discipline opérationnelle pour préserver cette marge."));
            }
        }

        // Healthy liquidity (current ratio > 1.5)
        if (f != null && f.getCurrentAssets() != null && f.getCurrentLiabilities() != null
                && f.getCurrentLiabilities().compareTo(ZERO) > 0) {
            BigDecimal cr = f.getCurrentAssets().divide(f.getCurrentLiabilities(), 4, RoundingMode.HALF_UP);
            if (cr.compareTo(new BigDecimal("1.5")) >= 0) {
                list.add(result(analysis, ResultType.STRENGTH, "Liquidité",
                        "Ratio de liquidité élevé",
                        String.format("Current Ratio de %.2f — capacité à honorer les dettes à court terme.",
                                cr.doubleValue()),
                        Severity.LOW,
                        "Optimiser l'utilisation de la trésorerie excédentaire pour maximiser les rendements."));
            }
        }

        // Low indebtedness
        if (f != null && f.getEquity() != null && f.getEquity().compareTo(ZERO) > 0) {
            BigDecimal totalDebt = safeAdd(f.getLongTermDebt(), f.getCurrentLiabilities());
            BigDecimal dte = totalDebt.divide(f.getEquity(), 4, RoundingMode.HALF_UP);
            if (dte.compareTo(new BigDecimal("0.5")) < 0) {
                list.add(result(analysis, ResultType.STRENGTH, "Solvabilité",
                        "Faible niveau d'endettement",
                        String.format("Ratio D/E de %.2f — structure financière solide avec peu de dette.",
                                dte.doubleValue()),
                        Severity.LOW,
                        "La capacité d'endettement restante peut financer une croissance externe."));
            }
        }

        // Good payment behavior
        if (f != null) {
            int incidents = f.getPaymentIncidents() != null ? f.getPaymentIncidents() : 0;
            int delay = f.getAveragePaymentDelay() != null ? f.getAveragePaymentDelay() : 0;
            if (incidents == 0 && delay <= 15) {
                list.add(result(analysis, ResultType.STRENGTH, "Comportement Paiement",
                        "Excellent historique de paiement",
                        "Aucun incident de paiement, délai moyen ≤ 15 jours.",
                        Severity.LOW,
                        "Valoriser cet historique pour négocier de meilleures conditions fournisseurs."));
            }
        }

        // Score strength
        if (score != null && score.compareTo(new BigDecimal("70")) >= 0) {
            list.add(result(analysis, ResultType.STRENGTH, "Score de Risque",
                    "Score de risque élevé",
                    String.format("Score global de %.0f/100 — entreprise classée risque faible.", score.doubleValue()),
                    Severity.LOW,
                    "Capitaliser sur ce score pour accéder à des financements avantageux."));
        }

        // Company maturity
        if (c != null && c.getIncorporationDate() != null) {
            int age = java.time.Period.between(c.getIncorporationDate(), LocalDate.now()).getYears();
            if (age >= 10) {
                list.add(result(analysis, ResultType.STRENGTH, "Maturité",
                        "Entreprise établie",
                        String.format("Ancienneté de %d ans — track-record longtemps établi sur le marché.", age),
                        Severity.LOW,
                        "Valoriser l'ancienneté auprès des investisseurs et partenaires."));
            }
        }

        if (list.isEmpty()) {
            list.add(result(analysis, ResultType.STRENGTH, "Fondamentaux",
                    "Structure opérationnelle en place",
                    "L'entreprise est active et dispose d'une structure opérationnelle fonctionnelle.",
                    Severity.LOW, "Consolider les fondamentaux pour construire des avantages concurrentiels."));
        }
        return list;
    }

    // ── WEAKNESSES ───────────────────────────────────────────────────────────

    private List<AnalysisResult> generateWeaknesses(
            FinancialAnalysis analysis, CompanyDTO c, FinancialDataDTO f) {
        List<AnalysisResult> list = new ArrayList<>();

        // Negative or very low equity
        if (f != null && f.getEquity() != null && f.getEquity().compareTo(ZERO) < 0) {
            list.add(result(analysis, ResultType.WEAKNESS, "Solvabilité",
                    "Capitaux propres négatifs",
                    String.format("Equity de %.0f — indicateur critique d'insolvabilité potentielle.",
                            f.getEquity().doubleValue()),
                    Severity.CRITICAL,
                    "Recapitalisation urgente ou restructuration financière nécessaire."));
        }

        // Low liquidity
        if (f != null && f.getCurrentAssets() != null && f.getCurrentLiabilities() != null
                && f.getCurrentLiabilities().compareTo(ZERO) > 0) {
            BigDecimal cr = f.getCurrentAssets().divide(f.getCurrentLiabilities(), 4, RoundingMode.HALF_UP);
            if (cr.compareTo(BigDecimal.ONE) < 0) {
                list.add(result(analysis, ResultType.WEAKNESS, "Liquidité",
                        "Ratio de liquidité insuffisant",
                        String.format(
                                "Current Ratio de %.2f — incapacité potentielle à couvrir les obligations à court terme.",
                                cr.doubleValue()),
                        Severity.HIGH,
                        "Mettre en place un plan de gestion du besoin en fonds de roulement (BFR)."));
            }
        }

        // Net losses
        if (f != null && f.getNetResult() != null && f.getNetResult().compareTo(ZERO) < 0) {
            list.add(result(analysis, ResultType.WEAKNESS, "Rentabilité",
                    "Résultat net négatif",
                    String.format("Perte nette de %.0f — l'entreprise n'est pas profitable.",
                            f.getNetResult().doubleValue()),
                    Severity.HIGH,
                    "Analyse des coûts et optimisation des revenus indispensable à court terme."));
        }

        // Payment incidents
        if (f != null && f.getPaymentIncidents() != null && f.getPaymentIncidents() > 0) {
            list.add(result(analysis, ResultType.WEAKNESS, "Comportement Paiement",
                    "Incidents de paiement enregistrés",
                    String.format("%d incident(s) de paiement — signal d'alerte pour les créanciers.",
                            f.getPaymentIncidents()),
                    Severity.HIGH,
                    "Établir un plan de remboursement structuré et communiquer avec les fournisseurs."));
        }

        // High delays
        if (f != null && f.getAveragePaymentDelay() != null && f.getAveragePaymentDelay() > 30) {
            list.add(result(analysis, ResultType.WEAKNESS, "Comportement Paiement",
                    "Délai de paiement élevé",
                    String.format("Délai moyen de %d jours — supérieur au seuil de 30 jours.",
                            f.getAveragePaymentDelay()),
                    Severity.MEDIUM,
                    "Mettre en place un processus de relance structuré pour améliorer les encaissements."));
        }

        // High debt
        if (f != null && f.getEquity() != null && f.getEquity().compareTo(ZERO) > 0) {
            BigDecimal totalDebt = safeAdd(f.getLongTermDebt(), f.getCurrentLiabilities());
            BigDecimal dte = totalDebt.divide(f.getEquity(), 4, RoundingMode.HALF_UP);
            if (dte.compareTo(new BigDecimal("2.0")) >= 0) {
                list.add(result(analysis, ResultType.WEAKNESS, "Solvabilité",
                        "Endettement excessif",
                        String.format("Ratio D/E de %.2f — levier financier dangereux.", dte.doubleValue()),
                        Severity.HIGH,
                        "Réduire la dette ou procéder à une augmentation de capital."));
            }
        }

        // Young company risk
        if (c != null && c.getIncorporationDate() != null) {
            int age = java.time.Period.between(c.getIncorporationDate(), LocalDate.now()).getYears();
            if (age < 2) {
                list.add(result(analysis, ResultType.WEAKNESS, "Maturité",
                        "Entreprise très récente",
                        String.format("Seulement %d an(s) d'activité — historique financier limité pour l'évaluation.",
                                age),
                        Severity.MEDIUM,
                        "Documenter rigoureusement les performances pour construire un historique crédible."));
            }
        }

        if (list.isEmpty()) {
            list.add(result(analysis, ResultType.WEAKNESS, "Analyse",
                    "Données incomplètes",
                    "Certaines données financières sont manquantes, limitant l'analyse complète.",
                    Severity.LOW, "Compléter toutes les données financières pour une analyse plus précise."));
        }
        return list;
    }

    // ── OPPORTUNITIES________________________________________

    private List<AnalysisResult> generateOpportunities(
            FinancialAnalysis analysis, CompanyDTO c, FinancialDataDTO f, BigDecimal score) {
        List<AnalysisResult> list = new ArrayList<>();

        // Capacity to take on debt (low debt-to-equity)
        if (f != null && f.getEquity() != null && f.getEquity().compareTo(ZERO) > 0) {
            BigDecimal totalDebt = safeAdd(f.getLongTermDebt(), f.getCurrentLiabilities());
            BigDecimal dte = totalDebt.divide(f.getEquity(), 4, RoundingMode.HALF_UP);
            if (dte.compareTo(new BigDecimal("0.8")) < 0) {
                list.add(result(analysis, ResultType.OPPORTUNITY, "Croissance",
                        "Capacité d'endettement disponible",
                        "Ratio D/E faible offrant une marge pour financer des acquisitions ou investissements.",
                        Severity.LOW,
                        "Explorer des opportunités d'expansion via financement externe à faible coût."));
            }
        }

        // High cash position for investment
        if (f != null && f.getCash() != null && f.getCash().compareTo(ZERO) > 0
                && f.getRevenue() != null && f.getRevenue().compareTo(ZERO) > 0) {
            BigDecimal cashToRev = f.getCash().divide(f.getRevenue(), 4, RoundingMode.HALF_UP);
            if (cashToRev.compareTo(new BigDecimal("0.15")) >= 0) {
                list.add(result(analysis, ResultType.OPPORTUNITY, "Trésorerie",
                        "Trésorerie disponible pour investissement",
                        String.format("Trésorerie représentant %.1f%% du CA — capacité d'investissement significative.",
                                cashToRev.multiply(BigDecimal.valueOf(100)).doubleValue()),
                        Severity.LOW,
                        "Envisager des investissements en R&D, marketing ou acquisitions stratégiques."));
            }
        }

        // Favorable sector
        if (c != null && c.getIndustrySector() != null) {
            String sector = c.getIndustrySector().toLowerCase();
            if (sector.contains("tech") || sector.contains("software")) {
                list.add(result(analysis, ResultType.OPPORTUNITY, "Secteur",
                        "Secteur technologique en forte croissance",
                        "Le secteur technologique bénéficie de tendances structurelles favorables (IA, cloud, transformation numérique).",
                        Severity.LOW,
                        "Investir dans des produits différenciants pour capter la croissance sectorielle."));
            }
        }

        // Score improvement potential
        if (score != null && score.compareTo(new BigDecimal("40")) >= 0
                && score.compareTo(new BigDecimal("70")) < 0) {
            list.add(result(analysis, ResultType.OPPORTUNITY, "Score de Risque",
                    "Amélioration significative du score possible",
                    String.format("Score actuel de %.0f/100 — des actions ciblées pourraient améliorer la notation.",
                            score.doubleValue()),
                    Severity.MEDIUM,
                    "Réduire les incidents de paiement et améliorer la liquidité pour progresser vers le grade BBB."));
        }

        if (list.isEmpty()) {
            list.add(result(analysis, ResultType.OPPORTUNITY, "Marché",
                    "Potentiel d'optimisation opérationnelle",
                    "L'entreprise peut améliorer ses processus internes pour gagner en efficacité.",
                    Severity.LOW,
                    "Investir dans des outils d'automatisation et de gestion pour améliorer la productivité."));
        }
        return list;
    }

    // ── THREATS______________________

    private List<AnalysisResult> generateThreats(
            FinancialAnalysis analysis, CompanyDTO c, FinancialDataDTO f) {
        List<AnalysisResult> list = new ArrayList<>();

        // Liquidity risk
        if (f != null && f.getCurrentAssets() != null && f.getCurrentLiabilities() != null
                && f.getCurrentLiabilities().compareTo(ZERO) > 0) {
            BigDecimal cr = f.getCurrentAssets().divide(f.getCurrentLiabilities(), 4, RoundingMode.HALF_UP);
            if (cr.compareTo(new BigDecimal("1.2")) < 0) {
                list.add(result(analysis, ResultType.THREAT, "Liquidité",
                        "Risque de défaut à court terme",
                        String.format("Current Ratio de %.2f — proche de la zone critique (< 1.0).", cr.doubleValue()),
                        Severity.HIGH,
                        "Accélérer les encaissements et négocier des délais fournisseurs plus longs."));
            }
        }

        // Interest rate risk (high debt)
        if (f != null && f.getLongTermDebt() != null && f.getLongTermDebt().compareTo(ZERO) > 0) {
            if (f.getFinancialExpenses() == null || f.getFinancialExpenses().compareTo(ZERO) == 0) {
                list.add(result(analysis, ResultType.THREAT, "Taux d'Intérêt",
                        "Exposition à la hausse des taux d'intérêt",
                        "La dette à long terme expose l'entreprise au risque de taux en cas de refinancement.",
                        Severity.MEDIUM,
                        "Envisager de fixer le taux sur la dette principale ou de réduire l'exposition variable."));
            }
        }

        // Sector risk
        if (c != null && c.getIndustrySector() != null) {
            String sector = c.getIndustrySector().toLowerCase();
            if (sector.contains("construction") || sector.contains("retail")) {
                list.add(result(analysis, ResultType.THREAT, "Secteur",
                        "Secteur à risque cyclique élevé",
                        "Le secteur présente une forte cyclicité et une sensibilité aux conditions macroéconomiques.",
                        Severity.MEDIUM,
                        "Diversifier les sources de revenus pour réduire l'exposition au cycle économique."));
            }
        }

        // Payment behavior as threat
        if (f != null && f.getAveragePaymentDelay() != null && f.getAveragePaymentDelay() > 60) {
            list.add(result(analysis, ResultType.THREAT, "Relations Fournisseurs",
                    "Risque de détérioration des relations fournisseurs",
                    String.format("Délai de paiement de %d jours — risque de rupture approvisionnement.",
                            f.getAveragePaymentDelay()),
                    Severity.HIGH,
                    "Priorité à régulariser les paiements fournisseurs stratégiques."));
        }

        if (list.isEmpty()) {
            list.add(result(analysis, ResultType.THREAT, "Marché",
                    "Pression concurrentielle",
                    "L'environnement concurrentiel peut peser sur les marges à moyen terme.",
                    Severity.LOW,
                    "Surveiller les évolutions du marché et adapter la stratégie de prix en conséquence."));
        }
        return list;
    }

    //HELPERS_____
    private AnalysisResult result(FinancialAnalysis analysis, ResultType type,
            String category, String title, String description,
            Severity severity, String recommendation) {
        AnalysisResult r = new AnalysisResult();
        r.setAnalysis(analysis);
        r.setResultType(type);
        r.setCategory(category);
        r.setTitle(title);
        r.setDescription(description);
        r.setSeverity(severity);
        r.setRecommendation(recommendation);
        return r;
    }

    private OverallHealth deriveHealth(FinancialDataDTO f, BigDecimal score) {
        if (score != null) {
            if (score.compareTo(new BigDecimal("70")) >= 0)
                return OverallHealth.GOOD;
            if (score.compareTo(new BigDecimal("40")) >= 0)
                return OverallHealth.MODERATE;
            return OverallHealth.POOR;
        }
        if (f == null)
            return OverallHealth.MODERATE;
        if (f.getNetResult() != null && f.getNetResult().compareTo(ZERO) > 0
                && f.getEquity() != null && f.getEquity().compareTo(ZERO) > 0)
            return OverallHealth.GOOD;
        if (f.getEquity() != null && f.getEquity().compareTo(ZERO) < 0)
            return OverallHealth.POOR;
        return OverallHealth.MODERATE;
    }

    private CompanyDTO fetchCompany(Long companyId) {
        try {
            return companyClient.getCompanyById(companyId);
        } catch (Exception e) {
            log.warn("Could not fetch company {}: {}", companyId, e.getMessage());
            CompanyDTO fb = new CompanyDTO();
            fb.setId(companyId);
            return fb;
        }
    }

    private FinancialDataDTO fetchFinancials(Long companyId) {
        try {
            return financialsClient.getLatestFinancials(companyId);
        } catch (Exception e) {
            log.warn("Could not fetch financials for {}: {}", companyId, e.getMessage());
            return new FinancialDataDTO();
        }
    }

    private BigDecimal safeAdd(BigDecimal a, BigDecimal b) {
        return (a != null ? a : ZERO).add(b != null ? b : ZERO);
    }
}
