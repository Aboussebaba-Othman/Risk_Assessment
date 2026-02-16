package com.riskassessment.alertservice.service;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AlertService {

    private final AlertRepository alertRepository;

    @Transactional
    public Alert createAndSendAlert(String recipient, String subject, String message, Alert.AlertType type) {
        Alert alert = new Alert();
        alert.setRecipient(recipient);
        alert.setSubject(subject);
        alert.setMessage(message);
        alert.setType(type);
        alert.setStatus(Alert.AlertStatus.PENDING);
        alert.setRetryCount(0); // Initialize retry count

        alert = alertRepository.save(alert);
        log.info("Alert persisted with ID: {} (Status: PENDING)", alert.getId());
        return alert;
    }
}
