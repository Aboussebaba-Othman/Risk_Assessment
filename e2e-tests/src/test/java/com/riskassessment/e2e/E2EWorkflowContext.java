package com.riskassessment.e2e;

import lombok.Data;

@Data
public class E2EWorkflowContext {
    private String accessToken;
    private Long companyId;
    private String analysisStatus;
    private Double overallScore;
}
