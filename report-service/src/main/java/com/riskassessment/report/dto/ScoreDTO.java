package com.riskassessment.report.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ScoreDTO {
    private BigDecimal overallScore;
    private String riskLevel;
    private String riskRating;
}
