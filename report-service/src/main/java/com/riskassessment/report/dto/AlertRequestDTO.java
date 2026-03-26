package com.riskassessment.report.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AlertRequestDTO {
    private Long companyId;
    private Long tenantId;
    private String recipient;
    private String subject;
    private String message;
    private String type;
    private String severity;
}
