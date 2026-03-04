package com.riskassessment.alertservice.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ScoreDTO {
    private Long id;
    private Long companyId;
    private BigDecimal overallScore;
    private String riskLevel;
    private String riskRating;
    private LocalDateTime scoredAt;
}
