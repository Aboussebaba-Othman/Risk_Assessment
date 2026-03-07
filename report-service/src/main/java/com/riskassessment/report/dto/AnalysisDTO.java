package com.riskassessment.report.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
public class AnalysisDTO {
    private Long id;
    private Long companyId;
    private String analysisType;
    private String overallHealth; // GOOD / MODERATE / POOR
    private String status;

    private BigDecimal revenue;
    private BigDecimal netProfit;
    private BigDecimal assets;
    private BigDecimal liabilities;
    private BigDecimal equity;
    private BigDecimal cashFlow;

    private String notes;
    private LocalDateTime createdAt;

    private List<AnalysisResultDTO> results;

    private String summary;
    private String recommendation;
}
