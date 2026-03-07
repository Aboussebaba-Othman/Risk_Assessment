package com.riskassessment.scoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoringResult {
    private int finalScore;
    private int financialScore;
    private int paymentScore;
    private int contextScore;
    private String scoringNotes;
}
