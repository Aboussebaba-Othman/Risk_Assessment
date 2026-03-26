package com.riskassessment.scoring.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreCalculatedEvent {
    private Long companyId;
    private Long tenantId;
    private Long scoreId;

    private BigDecimal overallScore;
    private BigDecimal financialScore;
    private BigDecimal operationalScore;
    private BigDecimal marketScore;

    private String riskLevel;
    private String riskRating;
    private LocalDateTime calculatedAt;

    private Map<String, Double> ratioBreakdown;
}
