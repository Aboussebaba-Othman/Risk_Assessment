package com.riskassessment.analysis.dto;

import com.riskassessment.analysis.enums.AnalysisStatus;
import com.riskassessment.analysis.enums.AnalysisType;
import com.riskassessment.analysis.enums.OverallHealth;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FinancialAnalysisDTO {
    private Long id;

    @NotNull(message = "Company ID is required")
    private Long companyId;

    @NotNull(message = "Analysis type is required")
    private AnalysisType analysisType;

    @NotNull(message = "Period start date is required")
    private LocalDate periodStart;

    @NotNull(message = "Period end date is required")
    private LocalDate periodEnd;

    private AnalysisStatus status;
    private OverallHealth overallHealth;
    private BigDecimal revenue;
    private BigDecimal netProfit;
    private BigDecimal assets;
    private BigDecimal liabilities;
    private BigDecimal equity;
    private BigDecimal cashFlow;
    private String notes;
    private LocalDateTime createdAt;
    private List<AnalysisResultDTO> results;
}
