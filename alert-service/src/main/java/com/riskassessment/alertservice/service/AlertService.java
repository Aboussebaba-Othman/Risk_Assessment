package com.riskassessment.alertservice.service;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertType;

import java.util.List;

public interface AlertService {

    AlertResponseDTO createAndSendAlert(Long companyId, String recipient, String subject,
                                        String message, AlertType type,
                                        AlertSeverity severity);

    AlertResponseDTO createAndSendAlert(String recipient, String subject,
                                        String message, AlertType type);

    AlertResponseDTO createAlertFromRequest(AlertRequestDTO request);

    AlertResponseDTO getAlertById(Long id);

    List<AlertResponseDTO> getAlertsByCompany(Long companyId);

    List<AlertResponseDTO> getAllAlerts();
}
