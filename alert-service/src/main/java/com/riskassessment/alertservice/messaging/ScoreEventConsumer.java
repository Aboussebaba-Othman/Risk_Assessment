package com.riskassessment.alertservice.messaging;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.event.ScoreCalculatedEvent;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreEventConsumer {

    private final AlertService alertService;

    @RabbitListener(queues = RabbitMQConfig.ALERT_QUEUE)
    public void handleScoreCalculated(ScoreCalculatedEvent event) {
        log.info("Received ScoreCalculatedEvent: companyId={} score={} riskLevel={}",
                event.getCompanyId(), event.getOverallScore(), event.getRiskLevel());

        BigDecimal score = event.getOverallScore();
        if (score == null)
            return;

        if (score.compareTo(new BigDecimal("20")) < 0) {
            // CRITICAL — score < 20 → imminent default risk
            alertService.createAndSendAlert(
                    event.getCompanyId(),
                    "risk@riskassessment.com",
                    "🚨 RISQUE CRITIQUE — Société #" + event.getCompanyId(),
                    buildCriticalMessage(event),
                    Alert.AlertType.SCORE_CHANGE,
                    Alert.AlertSeverity.CRITICAL);
            log.warn("CRITICAL alert created for companyId={} score={}", event.getCompanyId(), score);

        } else if (score.compareTo(new BigDecimal("40")) < 0) {
            // HIGH — score between 20 and 40
            alertService.createAndSendAlert(
                    event.getCompanyId(),
                    "risk@riskassessment.com",
                    "⚠️ RISQUE ÉLEVÉ — Société #" + event.getCompanyId(),
                    buildHighRiskMessage(event),
                    Alert.AlertType.SCORE_CHANGE,
                    Alert.AlertSeverity.HIGH);
            log.warn("HIGH alert created for companyId={} score={}", event.getCompanyId(), score);

        } else {
            log.debug("Score {} for company {} does not trigger an alert (threshold: < 40)",
                    score, event.getCompanyId());
        }
    }

    private String buildCriticalMessage(ScoreCalculatedEvent e) {
        return String.format(
                "<h2>⚠️ ALERTE CRITIQUE</h2>" +
                        "<p>La société <strong>#%d</strong> a un score de <strong>%.0f/100</strong> " +
                        "(Notation: <strong>%s</strong>).</p>" +
                        "<p>Risque de défaut imminent. Intervention urgente requise.</p>" +
                        "<ul><li>Score financier: %.1f/40</li>" +
                        "<li>Score paiement: %.1f/35</li>" +
                        "<li>Score contexte: %.1f/25</li></ul>" +
                        "<p>Calculé le : %s</p>",
                e.getCompanyId(), e.getOverallScore().doubleValue(), e.getRiskRating(),
                safe(e.getFinancialScore()), safe(e.getOperationalScore()), safe(e.getMarketScore()),
                e.getCalculatedAt());
    }

    private String buildHighRiskMessage(ScoreCalculatedEvent e) {
        return String.format(
                "<h2>⚠️ ALERTE RISQUE ÉLEVÉ</h2>" +
                        "<p>La société <strong>#%d</strong> a un score de <strong>%.0f/100</strong> " +
                        "(Notation: <strong>%s</strong>).</p>" +
                        "<p>Une surveillance renforcée est recommandée.</p>" +
                        "<ul><li>Score financier: %.1f/40</li>" +
                        "<li>Score paiement: %.1f/35</li>" +
                        "<li>Score contexte: %.1f/25</li></ul>",
                e.getCompanyId(), e.getOverallScore().doubleValue(), e.getRiskRating(),
                safe(e.getFinancialScore()), safe(e.getOperationalScore()), safe(e.getMarketScore()));
    }

    private double safe(BigDecimal v) {
        return v != null ? v.doubleValue() : 0.0;
    }
}
