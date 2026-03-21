package com.riskassessment.analysis.dto;

import com.riskassessment.analysis.enums.ResultType;
import com.riskassessment.analysis.enums.Severity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnalysisResultDTO {
    private Long id;

    @NotNull(message = "Result type is required")
    private ResultType resultType;

    @NotBlank(message = "Category cannot be blank")
    private String category;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private Severity severity;
    private String recommendation;
}
