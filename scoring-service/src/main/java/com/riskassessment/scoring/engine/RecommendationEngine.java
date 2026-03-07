package com.riskassessment.scoring.engine;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.enums.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@Slf4j
public class RecommendationEngine {

    public RecommendationDTO recommend(int score, RiskLevel riskLevel, List<String> warnings) {
        RecommendationDTO.RecommendationDTOBuilder builder = RecommendationDTO.builder()
                .score(score)
                .riskLevel(riskLevel)
                .justification(new ArrayList<>(warnings == null ? List.of() : warnings));

        if (score >= 90) {
            builder
                    .decision("ACCORD")
                    .decisionLabel("Accord sans réserve")
                    .creditLimitPolicy("Illimité")
                    .maxPaymentDays(90)
                    .guaranteesRequired("Aucune")
                    .defaultRateRange("<1%");
            addJustification(builder, "Score excellent (≥90) — risque de défaut quasi nul.");

        } else if (score >= 75) {
            builder
                    .decision("ACCORD_RESERVE")
                    .decisionLabel(" Accord sous réserve")
                    .creditLimitPolicy("100% de la demande")
                    .maxPaymentDays(90)
                    .guaranteesRequired("Surveillance légère recommandée")
                    .defaultRateRange("1%-3%");
            addJustification(builder, "Score faible risque (75-89) — suivi trimestriel conseillé.");

        } else if (score >= 60) {
            builder
                    .decision("ACCORD_CONDITIONNEL")
                    .decisionLabel("Accord conditionnel")
                    .creditLimitPolicy("100% de la demande")
                    .maxPaymentDays(90)
                    .guaranteesRequired("Vérification périodique de la solvabilité")
                    .defaultRateRange("3%-8%");
            addJustification(builder, "Score modéré (60-74) — révision recommandée tous les 3 mois.");

        } else if (score >= 40) {
            builder
                    .decision("ACCORD_CONDITIONNEL")
                    .decisionLabel(" Accord conditionnel — limité à 70%")
                    .creditLimitPolicy("70% de la demande")
                    .maxPaymentDays(60)
                    .guaranteesRequired("Caution bancaire ou garantie personnelle requise")
                    .defaultRateRange("8%-15%");
            addJustification(builder, "Score moyen (40-59) — exposition limitée, garantie exigée.");

        } else if (score >= 25) {
            builder
                    .decision("ACCORD_RESTRICTIF")
                    .decisionLabel("Accord restrictif — limité à 40%")
                    .creditLimitPolicy("40% de la demande")
                    .maxPaymentDays(30)
                    .guaranteesRequired("Paiement anticipé 50% avant livraison")
                    .defaultRateRange("15%-30%");
            addJustification(builder,
                    "Score élevé risque (25-39) — crédit très limité, paiement anticipé obligatoire.");

        } else {
            builder
                    .decision("REFUS")
                    .decisionLabel("Refus — avec alternatives")
                    .creditLimitPolicy("Maximum 10 000 MAD")
                    .maxPaymentDays(15)
                    .guaranteesRequired("Paiement anticipé 100% (proforma uniquement)")
                    .defaultRateRange(">30%");
            addJustification(builder, "Score critique (<25) — risque de défaut très élevé.");

            if (riskLevel == RiskLevel.CRITICAL) {
                addJustification(builder,
                        " REFUS FERME si capitaux propres négatifs — non modifiable même par administrateur.");
            }
        }

        RecommendationDTO rec = builder.build();
        log.info("Recommendation issued: score={} decision={} limit={} days={}",
                score, rec.getDecision(), rec.getCreditLimitPolicy(), rec.getMaxPaymentDays());
        return rec;
    }

    public RecommendationDTO recommend(int score, RiskLevel riskLevel) {
        return recommend(score, riskLevel, List.of());
    }

    private void addJustification(RecommendationDTO.RecommendationDTOBuilder builder, String text) {
    }

    public RecommendationDTO recommendWithJustification(int score, RiskLevel riskLevel, List<String> extraWarnings) {
        List<String> warnings = new ArrayList<>(extraWarnings == null ? List.of() : extraWarnings);
        RecommendationDTO rec = recommend(score, riskLevel, warnings);

        List<String> justifications = new ArrayList<>(rec.getJustification());
        if (score >= 90)
            justifications.add("Score excellent (≥90) — risque de défaut quasi nul.");
        else if (score >= 75)
            justifications.add("Score faible risque (75-89) — suivi trimestriel conseillé.");
        else if (score >= 60)
            justifications.add("Score modéré (60-74) — révision recommandée tous les 3 mois.");
        else if (score >= 40)
            justifications.add("Score moyen (40-59) — exposition limitée, garantie exigée.");
        else if (score >= 25)
            justifications.add("Score élevé risque (25-39) — crédit très limité.");
        else
            justifications.add("Score critique (<25) — risque de défaut très élevé.");

        rec.setJustification(justifications);
        return rec;
    }
}
