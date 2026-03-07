package com.riskassessment.scoring.dto;

import com.riskassessment.scoring.entity.enums.RiskLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {

    private int score;
    private RiskLevel riskLevel;
    private String decision;
    private String decisionLabel;
    private String creditLimitPolicy;
    private Integer maxPaymentDays;
    private String guaranteesRequired;
    private String defaultRateRange;
    private List<String> justification;
}
