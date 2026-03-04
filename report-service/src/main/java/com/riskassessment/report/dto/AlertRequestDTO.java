package com.riskassessment.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertRequestDTO {
    private String recipient;
    private String subject;
    private String message;
    private String type;
}
