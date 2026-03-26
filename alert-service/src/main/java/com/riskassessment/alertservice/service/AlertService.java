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

    AlertResponseDTO getAlertById(Long id);

    void markAsRead(Long id);

    long countUnreadAlerts(Long tenantId);

    List<AlertResponseDTO> getAlertsByCompany(Long companyId);

    List<AlertResponseDTO> getAllAlerts();
}
