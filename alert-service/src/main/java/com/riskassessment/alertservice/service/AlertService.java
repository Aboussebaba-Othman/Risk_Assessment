package com.riskassessment.alertservice.service;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertType;

import java.util.List;

public interface AlertService {

    AlertResponseDTO createAndSendAlert(Long companyId, Long tenantId, String recipient, String subject,
                                        String message, AlertType type,
                                        AlertSeverity severity);

    AlertResponseDTO createAlertFromRequest(AlertRequestDTO request);

    AlertResponseDTO getAlertById(Long id, Long tenantId);

    void markAsRead(Long id, Long tenantId);

    void markAllAsRead(Long tenantId);

    long countUnreadAlerts(Long tenantId);

    List<AlertResponseDTO> getAlertsByCompany(Long companyId, Long tenantId);

    List<AlertResponseDTO> getAllAlerts(Long tenantId);

    List<AlertResponseDTO> getAllAlertsForSystem();
}
