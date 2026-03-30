package com.riskassessment.alertservice.service.impl;

import com.riskassessment.alertservice.mapper.AlertMapper;
import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertStatus;
import com.riskassessment.alertservice.enums.AlertType;
import com.riskassessment.alertservice.exception.AlertNotFoundException;
import com.riskassessment.alertservice.repository.AlertRepository;
import com.riskassessment.alertservice.service.AlertService;
import com.riskassessment.alertservice.service.AlertSseService;
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
    private final AlertSseService sseService;

    @Override
    @Transactional
    public AlertResponseDTO createAndSendAlert(Long companyId, Long tenantId, String recipient, String subject, String message, AlertType type, AlertSeverity severity) {
        Alert alert = Alert.builder()
                .companyId(companyId)
                .tenantId(tenantId)
                .recipient(recipient)
                .subject(subject)
                .message(message)
                .type(type)
                .severity(severity)
                .status(AlertStatus.PENDING)
                .build();

        alert = alertRepository.save(alert);
        log.info("Alert persisted id={} companyId={} tenantId={} severity={} type={}", alert.getId(),
                companyId, tenantId, severity, type);
                
        AlertResponseDTO responseDto = alertMapper.toDto(alert);
        sseService.dispatch(responseDto);
        return responseDto;
    }



    @Override
    @Transactional
    public AlertResponseDTO createAlertFromRequest(AlertRequestDTO request) {
        return createAndSendAlert(
                request.getCompanyId(),
                request.getTenantId(), 
                request.getRecipient(), 
                request.getSubject(), 
                request.getMessage(), 
                request.getType(), 
                request.getSeverity() != null ? request.getSeverity() : AlertSeverity.WARNING
        );
    }

    @Override
    public AlertResponseDTO getAlertById(Long id, Long tenantId) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException("Alert with ID " + id + " not found"));
        
        if (tenantId != null && !tenantId.equals(alert.getTenantId())) {
            log.warn("Tenant isolation: tenantId={} tried to access alert {} owned by tenantId={}", 
                    tenantId, id, alert.getTenantId());
            throw new AlertNotFoundException("Alert not found");
        }
        
        return alertMapper.toDto(alert);
    }

    @Override
    @Transactional
    public void markAsRead(Long id, Long tenantId) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new AlertNotFoundException("Alert with ID " + id + " not found"));
        
        if (tenantId != null && !tenantId.equals(alert.getTenantId())) {
            log.warn("Tenant isolation: tenantId={} tried to mark as read alert {} owned by tenantId={}", 
                    tenantId, id, alert.getTenantId());
            throw new AlertNotFoundException("Alert not found");
        }
        
        if (!alert.isRead()) {
            alert.setRead(true);
            alert.setReadAt(java.time.LocalDateTime.now());
            alertRepository.save(alert);
            log.info("Alert {} marked as read for tenantId={}", id, tenantId);
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void markAllAsRead(Long tenantId) {
        log.info("Marking all alerts as read for tenantId={}", tenantId);
        alertRepository.markAllAsReadByTenantId(tenantId);
    }

    @Override
    public long countUnreadAlerts(Long tenantId) {
        return alertRepository.countByTenantIdAndIsReadFalse(tenantId);
    }

    @Override
    public List<AlertResponseDTO> getAlertsByCompany(Long companyId, Long tenantId) {
        return alertMapper.toDtoList(alertRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .filter(a -> tenantId == null || tenantId.equals(a.getTenantId()))
                .collect(java.util.stream.Collectors.toList()));
    }

    @Override
    public List<AlertResponseDTO> getAllAlerts(Long tenantId) {
        if (tenantId == null) return java.util.Collections.emptyList();
        return alertMapper.toDtoList(alertRepository.findByTenantIdOrderByCreatedAtDesc(tenantId));
    }

    @Override
    public List<AlertResponseDTO> getAllAlertsForSystem() {
        return alertMapper.toDtoList(alertRepository.findAllByOrderByCreatedAtDesc());
    }
}
