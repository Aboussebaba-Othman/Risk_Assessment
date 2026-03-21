package com.riskassessment.alertservice.dto;

import com.riskassessment.alertservice.enums.AlertType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertRequestDTO {
    @NotBlank(message = "Recipient is mandatory")
    private String recipient;
    @NotBlank(message = "Subject is mandatory")
    private String subject;
    @NotBlank(message = "Message is mandatory")
    private String message;
    @NotNull(message = "Type is mandatory")
    private AlertType type;
}
