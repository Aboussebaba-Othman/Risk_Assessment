package com.riskassessment.report.dto;

import lombok.Data;

@Data
public class AnalysisResultDTO {
    private String resultType; // STRENGTH | WEAKNESS | OPPORTUNITY | THREAT
    private String category;
    private String title;
    private String description;
    private String severity;
    private String recommendation;
}
