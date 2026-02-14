package com.riskassessment.report.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyDTO {
    private Long id;
    private String name;
    private String taxId;
    private String industrySector;
    private String legalForm;
    private LocalDate incorporationDate;
    private String primaryAddress;
    private String country;
    private String contactEmail;
    private String contactPhone;
    private String website;
    private String status;
    private String riskLevel;
}
