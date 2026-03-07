package com.riskassessment.report.dto;

import lombok.Data;

@Data
public class RecommendationDTO {
    private int score;
    private String riskLevel;
    private String decision;
    private String decisionLabel;
    private String creditLimitPolicy;
    private Integer maxPaymentDays;
    private String guaranteesRequired;
    private String defaultRateRange;
}
