package com.riskassessment.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalysisDTO {
    private Long id;
    private Long companyId;
    private String status;
    private String overallSummary;
    private List<String> swotSummary; // Simplified for report
    private LocalDateTime completedAt;
}
