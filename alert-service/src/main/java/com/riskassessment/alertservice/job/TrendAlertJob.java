package com.riskassessment.alertservice.job;

import com.riskassessment.alertservice.client.ScoringClient;
import com.riskassessment.alertservice.dto.ScoreDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertType;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class TrendAlertJob {

    private final ScoringClient scoringClient;
    private final AlertService alertService;

    private static final int SCORE_DROP_THRESHOLD = 10; // alert if score drops ≥ 10 pts

    private final Set<String> alerted = new HashSet<>();


    @Scheduled(fixedDelay = 1_800_000) // every 30 minutes
    public void detectScoreDeclines() {
        log.debug("TrendAlertJob — scanning score histories for declines ≥ {} pts", SCORE_DROP_THRESHOLD);

        // Gather known company IDs from existing alerts
        List<Long> companyIds = getTrackedCompanyIds();
        if (companyIds.isEmpty()) {
            log.debug("TrendAlertJob — no companies with alerts yet, skipping.");
            return;
        }

        for (Long companyId : companyIds) {
            try {
                checkCompanyTrend(companyId);
            } catch (Exception e) {
                log.warn("TrendAlertJob — error checking companyId={}: {}", companyId, e.getMessage());
            }
        }
    }

    private void checkCompanyTrend(Long companyId) {
        List<ScoreDTO> history;
        try {
            history = scoringClient.getScoreHistory(companyId);
        } catch (Exception e) {
            log.debug("Could not fetch score history for companyId={}: {}", companyId, e.getMessage());
            return;
        }

        if (history == null || history.size() < 2)
            return;

        // History is sorted DESC (newest first from scoring-service)
        ScoreDTO latest = history.get(0);
        ScoreDTO previous = history.get(1);

        if (latest.getOverallScore() == null || previous.getOverallScore() == null)
            return;

        BigDecimal drop = previous.getOverallScore().subtract(latest.getOverallScore());
        String alertKey = companyId + ":" + latest.getId();

        if (drop.compareTo(BigDecimal.valueOf(SCORE_DROP_THRESHOLD)) >= 0
                && !alerted.contains(alertKey)) {

            alerted.add(alertKey);

            String subject = String.format("📉 Score en baisse — Société #%d", companyId);
            String message = String.format(
                    "<h2>📉 Alerte Tendance — Dégradation du Score</h2>" +
                            "<p>La société <strong>#%d</strong> a vu son score baisser de <strong>%.0f points</strong> "
                            +
                            "entre deux évaluations.</p>" +
                            "<ul>" +
                            "<li>Score précédent: <strong>%.0f/100</strong> (%s)</li>" +
                            "<li>Score actuel:    <strong>%.0f/100</strong> (%s)</li>" +
                            "<li>Baisse:          <strong>%.0f pts</strong></li>" +
                            "</ul>" +
                            "<p>Une surveillance renforcée est recommandée.</p>",
                    companyId,
                    drop.doubleValue(),
                    previous.getOverallScore().doubleValue(), previous.getRiskRating(),
                    latest.getOverallScore().doubleValue(), latest.getRiskRating(),
                    drop.doubleValue());

            AlertSeverity severity = drop.compareTo(BigDecimal.valueOf(20)) >= 0
                    ? AlertSeverity.HIGH
                    : AlertSeverity.WARNING;

            alertService.createAndSendAlert(
                    companyId,
                    latest.getTenantId(),
                    "risk@riskassessment.com",
                    subject,
                    message,
                    AlertType.SCORE_CHANGE,
                    severity);

            log.warn("TrendAlertJob — TREND alert created for companyId={} drop={} pts", companyId, drop);
        }
    }

  
    private List<Long> getTrackedCompanyIds() {
        try {
            return alertService.getAllAlerts().stream()
                    .filter(a -> a.getCompanyId() != null)
                    .map(AlertResponseDTO::getCompanyId)
                    .distinct()
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            log.warn("TrendAlertJob — could not retrieve company IDs: {}", e.getMessage());
            return new ArrayList<>();
        }
    }
}
