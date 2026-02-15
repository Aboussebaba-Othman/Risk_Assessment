package com.riskassessment.scoring.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinancialDataDTO {
    private BigDecimal revenue;
    private BigDecimal netResult;
    private BigDecimal currentAssets;
    private BigDecimal currentLiabilities;
    private BigDecimal equity;
    private BigDecimal longTermDebt;
}
