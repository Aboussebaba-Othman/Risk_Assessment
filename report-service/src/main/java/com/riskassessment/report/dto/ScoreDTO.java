package com.riskassessment.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoreDTO {
    private Long id;
    private Long companyId;
    private Integer score;
    private String riskLevel;
    private LocalDateTime calculatedAt;
    private String algorithmVersion;
}
