package com.riskassessment.alertservice.dto;

import com.riskassessment.alertservice.entity.Alert;
import lombok.Data;

@Data
public class AlertRequestDTO {
    private String recipient;
    private String subject;
    private String message;
    private Alert.AlertType type;
}
