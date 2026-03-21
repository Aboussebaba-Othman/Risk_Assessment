package com.riskassessment.alertservice.service.impl;

import com.riskassessment.alertservice.dto.AlertMapper;
import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertStatus;
import com.riskassessment.alertservice.enums.AlertType;
import com.riskassessment.alertservice.exception.AlertNotFoundException;
import com.riskassessment.alertservice.repository.AlertRepository;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;

    @Override
    @Transactional
    public AlertResponseDTO createAndSendAlert(Long companyId, String recipient, String subject, String message, AlertType type, AlertSeverity severity) {
        Alert alert = Alert.builder()
                .companyId(companyId)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .type(type)
                .severity(severity)
                .status(AlertStatus.PENDING)
                .build();

        alert = alertRepository.save(alert);
        log.info("Alert persisted id={} companyId={} severity={} type={}", alert.getId(),
                companyId, severity, type);
        return alertMapper.toDto(alert);
    }

    @Override
    @Transactional
    public AlertResponseDTO createAndSendAlert(String recipient, String subject, String message, AlertType type) {
        return createAndSendAlert(null, recipient, subject, message, type, AlertSeverity.WARNING);
    }

    @Override
    @Transactional
    public AlertResponseDTO createAlertFromRequest(AlertRequestDTO request) {
        return createAndSendAlert(
                null, 
                request.getRecipient(), 
                request.getSubject(), 
                request.getMessage(), 
                request.getType(), 
                AlertSeverity.WARNING
        );
    }

    @Override
    public AlertResponseDTO getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException("Alert with ID " + id + " not found"));
        return alertMapper.toDto(alert);
    }

    @Override
    public List<AlertResponseDTO> getAlertsByCompany(Long companyId) {
        return alertMapper.toDtoList(alertRepository.findByCompanyIdOrderByCreatedAtDesc(companyId));
    }

    @Override
    public List<AlertResponseDTO> getAllAlerts() {
        return alertMapper.toDtoList(alertRepository.findAllByOrderByCreatedAtDesc());
    }
}
