package com.riskassessment.alertservice.job;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.repository.AlertRepository;
import com.riskassessment.alertservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationJob {

    private final AlertRepository alertRepository;
    private final EmailService emailService;

    private static final int MAX_RETRIES = 3;

    @Scheduled(fixedDelay = 60000) // Run every minute
    public void processPendingAlerts() {
        log.debug("Checking for pending alerts...");
        List<Alert> pendingAlerts = alertRepository.findByStatusOrderByCreatedAtAsc(Alert.AlertStatus.PENDING);
        for (Alert alert : pendingAlerts) {
            processAlert(alert);
        }
    }

    private void processAlert(Alert alert) {
        try {
            log.info("Processing Alert ID: {}", alert.getId());
            emailService.sendEmail(alert.getRecipient(), alert.getSubject(), alert.getMessage());

            alert.setStatus(Alert.AlertStatus.SENT);
            alert.setSentAt(LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Alert ID: {} SENT successfully.", alert.getId());

        } catch (Exception e) {
            log.error("Failed to send Alert ID: {}", alert.getId(), e);
            handleFailure(alert, e.getMessage());
        }
    }

    private void handleFailure(Alert alert, String error) {
        int currentRetries = alert.getRetryCount() == null ? 0 : alert.getRetryCount();
        alert.setRetryCount(currentRetries + 1);

        if (alert.getRetryCount() >= MAX_RETRIES) {
            alert.setStatus(Alert.AlertStatus.FAILED);
            log.warn("Alert ID: {} marked as FAILED after {} attempts.", alert.getId(), MAX_RETRIES);
        } else {
            alert.setStatus(Alert.AlertStatus.PENDING);
        }
        alertRepository.save(alert);
    }
}
