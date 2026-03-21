package com.riskassessment.alertservice.messaging;

import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertType;
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

        boolean isCritical = score.compareTo(new BigDecimal("20")) < 0;
        boolean isHigh = !isCritical && score.compareTo(new BigDecimal("40")) < 0;

        if (isCritical || isHigh) {
            String title = isCritical ? "⚠️ ALERTE CRITIQUE" : "⚠️ ALERTE RISQUE ÉLEVÉ";
            String action = isCritical ? "Risque de défaut imminent. Intervention urgente requise." : "Une surveillance renforcée est recommandée.";
            AlertSeverity severity = isCritical ? AlertSeverity.CRITICAL : AlertSeverity.HIGH;
            String subject = (isCritical ? "🚨 RISQUE CRITIQUE" : "⚠️ RISQUE ÉLEVÉ") + " — Société #" + event.getCompanyId();

            alertService.createAndSendAlert(
                    event.getCompanyId(),
                    "risk@riskassessment.com",
                    subject,
                    buildAlertMessage(event, title, action),
                    AlertType.SCORE_CHANGE,
                    severity);

            log.warn("{} alert created for companyId={} score={}", severity, event.getCompanyId(), score);
        } else {
            log.debug("Score {} for company {} does not trigger an alert (threshold: < 40)",
                    score, event.getCompanyId());
        }
    }

    private String buildAlertMessage(ScoreCalculatedEvent e, String title, String actionInfo) {
        return String.format(
                "<h2>%s</h2>" +
                        "<p>La société <strong>#%d</strong> a un score de <strong>%.0f/100</strong> " +
                        "(Notation: <strong>%s</strong>).</p>" +
                        "<p>%s</p>" +
                        "<ul><li>Score financier: %.1f/40</li>" +
                        "<li>Score paiement: %.1f/35</li>" +
                        "<li>Score contexte: %.1f/25</li></ul>%s",
                title, e.getCompanyId(), e.getOverallScore().doubleValue(), e.getRiskRating(),
                actionInfo,
                safe(e.getFinancialScore()), safe(e.getOperationalScore()), safe(e.getMarketScore()),
                e.getCalculatedAt() != null ? String.format("<p>Calculé le : %s</p>", e.getCalculatedAt()) : "");
    }

    private double safe(BigDecimal v) {
        return v != null ? v.doubleValue() : 0.0;
    }
}
