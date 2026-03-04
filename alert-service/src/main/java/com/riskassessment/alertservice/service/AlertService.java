package com.riskassessment.alertservice.service;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public Alert createAndSendAlert(Long companyId, String recipient, String subject,
            String message, Alert.AlertType type,
            Alert.AlertSeverity severity) {
        Alert alert = Alert.builder()
                .companyId(companyId)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .type(type)
                .severity(severity)
                .status(Alert.AlertStatus.PENDING)
                .build();

        alert = alertRepository.save(alert);
        log.info("Alert persisted id={} companyId={} severity={} type={}", alert.getId(),
                companyId, severity, type);
        return alert;
    }

    /** Backwards-compatible overload without explicit companyId/severity */
    @Transactional
    public Alert createAndSendAlert(String recipient, String subject,
            String message, Alert.AlertType type) {
        return createAndSendAlert(null, recipient, subject, message, type, Alert.AlertSeverity.WARNING);
    }

    public List<Alert> getAlertsByCompany(Long companyId) {
        return alertRepository.findByCompanyIdOrderByCreatedAtDesc(companyId);
    }

    public List<Alert> getAllAlerts() {
        return alertRepository.findAllByOrderByCreatedAtDesc();
    }
}
