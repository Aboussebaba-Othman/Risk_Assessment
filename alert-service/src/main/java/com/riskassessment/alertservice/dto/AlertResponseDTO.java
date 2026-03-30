package com.riskassessment.alertservice.dto;

import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertStatus;
import com.riskassessment.alertservice.enums.AlertType;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@Data
public class AlertResponseDTO {
    private Long id;
    private Long companyId;
    private Long tenantId;
    private String subject;
    private String message;
    private AlertType type;
    private AlertSeverity severity;
    private AlertStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime sentAt;
    @JsonProperty("isRead")
    private boolean isRead;
    private LocalDateTime readAt;
}
