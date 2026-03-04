package com.riskassessment.scoring.strategy;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ScoringResult {
    private int overallScore;
    private double financialHealthScore;  // Max 40
    private double paymentBehaviorScore; // Max 35
    private double contextScore;          // Max 25
    private Map<String, Double> ratioBreakdown;
}
