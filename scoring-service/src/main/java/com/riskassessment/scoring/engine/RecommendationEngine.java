package com.riskassessment.scoring.engine;

import com.riskassessment.scoring.dto.RecommendationDTO;
import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.enums.RiskLevel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class RecommendationEngine {

    public RecommendationDTO recommendWithJustification(Score scoreInfo, List<String> warnings) {
        int scoreVal = scoreInfo.getOverallScore().intValue();
        RiskLevel riskLevel = scoreInfo.getRiskLevel();
        
        RecommendationDTO.RecommendationDTOBuilder builder = RecommendationDTO.builder()
                .score(scoreVal)
                .riskLevel(riskLevel);

        List<String> justifications = new ArrayList<>();

        // 1. Base Strategy according to overall CDC Score
        if (scoreVal >= 90) {
            builder.decision("ACCORD")
                   .decisionLabel("Accord sans réserve")
                   .creditLimitPolicy("Illimité")
                   .maxPaymentDays(90)
                   .guaranteesRequired("Aucune")
                   .defaultRateRange("<1%");
            justifications.add("Score global excellent (≥90) : Risque de défaut quasi nul.");
        } else if (scoreVal >= 75) {
            builder.decision("ACCORD_RESERVE")
                   .decisionLabel("Accord sous réserve")
                   .creditLimitPolicy("100% de la demande")
                   .maxPaymentDays(90)
                   .guaranteesRequired("Surveillance légère recommandée")
                   .defaultRateRange("1%-3%");
            justifications.add("Score global faible risque (75-89) : Profil sain, suivi trimestriel conseillé.");
        } else if (scoreVal >= 60) {
            builder.decision("ACCORD_CONDITIONNEL")
                   .decisionLabel("Accord conditionnel")
                   .creditLimitPolicy("100% de la demande")
                   .maxPaymentDays(90)
                   .guaranteesRequired("Vérification périodique de la solvabilité")
                   .defaultRateRange("3%-8%");
            justifications.add("Score global modéré (60-74) : Risque acceptable, révision recommandée tous les 3 mois.");
        } else if (scoreVal >= 40) {
            builder.decision("ACCORD_CONDITIONNEL")
                   .decisionLabel("Accord conditionnel — limité à 70%")
                   .creditLimitPolicy("70% de la demande")
                   .maxPaymentDays(60)
                   .guaranteesRequired("Caution bancaire ou garantie personnelle requise")
                   .defaultRateRange("8%-15%");
            justifications.add("Score global moyen (40-59) : Risque existant, exposition limitée recommandée avec garantie exigée.");
        } else if (scoreVal >= 25) {
            builder.decision("ACCORD_RESTRICTIF")
                   .decisionLabel("Accord restrictif — limité à 40%")
                   .creditLimitPolicy("40% de la demande")
                   .maxPaymentDays(30)
                   .guaranteesRequired("Paiement anticipé 50% avant livraison")
                   .defaultRateRange("15%-30%");
            justifications.add("Score global élevé risque (25-39) : Dégradation financière avérée. Crédit très limité, paiement anticipé partiel obligatoire.");
        } else {
            builder.decision("REFUS")
                   .decisionLabel("Refus — avec alternatives")
                   .creditLimitPolicy("Maximum 10 000 MAD")
                   .maxPaymentDays(15)
                   .guaranteesRequired("Paiement anticipé 100% (proforma uniquement)")
                   .defaultRateRange(">30%");
            justifications.add("Score global critique (<25) : Risque de défaut très élevé. Rejet automatique du crédit standard.");
        }

        // 2. Multi-factor Expert Overrides
        // Check Payment Behavior Sub-Score
        if (scoreInfo.getOperationalScore() != null && scoreInfo.getOperationalScore().intValue() < 40) {
            // Downgrade decision if payment behavior is poor despite a potentially okay global score
            if (scoreVal >= 60) {
                builder.decision("ACCORD_CONDITIONNEL")
                       .decisionLabel("Accord avec garanties de paiement")
                       .maxPaymentDays(30)
                       .guaranteesRequired("Garantie bancaire ferme requise du fait du comportement de paiement");
                justifications.add("Alerte Expert : Malgré la note globale OK, le sous-score de Comportement de Paiement est mauvais (<40). Délais de paiement réduits imposés.");
            }
        }

        // 3. Process Live Data Warnings (Vetos & Flags)
        boolean hasNegativeEquity = false;
        if (warnings != null && !warnings.isEmpty()) {
            for (String w : warnings) {
                justifications.add(w); // Add raw warning to the justification UI
                if (w.contains("Capitaux propres négatifs")) {
                    hasNegativeEquity = true;
                }
            }
        }

        // HARD VETO: Negative Equity immediately forces a hard refusal regardless of any other strengths
        if (hasNegativeEquity) {
            builder.decision("REFUS")
                   .decisionLabel("Refus Automatique (Règle VETO)")
                   .creditLimitPolicy("0 MAD (Gel total des encours)")
                   .maxPaymentDays(0)
                   .guaranteesRequired("Strictement 100% Paiement d'Avance (Proforma)")
                   .defaultRateRange(">50%");
            justifications.add("VETO EXPERT : REFUS FERME car capitaux propres négatifs détectés. Décision non contournable selon les standards CDC.");
        }

        builder.justification(justifications);
        RecommendationDTO rec = builder.build();
        
        log.info("Recommendation issued: score={} decision={} limit={} days={}",
                scoreVal, rec.getDecision(), rec.getCreditLimitPolicy(), rec.getMaxPaymentDays());
        
        return rec;
    }
}
