package com.riskassessment.report.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScoreDTO {
    private Long id;
    private Long companyId;
    private BigDecimal overallScore;
    private String riskLevel;
    private String riskRating;
    private String scoringMethod;
    private String notes;
    private String scoredAt;
}
