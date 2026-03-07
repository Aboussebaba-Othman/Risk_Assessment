package com.riskassessment.report.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class CompanyDTO {
    private Long id;
    private Long tenantId;
    private String name;
    private String registrationNumber;
    private String taxId;
    private String industry;
    private String legalForm;
    private LocalDate incorporationDate;
    private BigDecimal shareCapital;
    private String country;
    private String city;
    private String address;
    private String phone;
    private String email;
    private String website;
    private Integer employeeCount;
    private BigDecimal annualRevenue;
    private String currency;
    private String status;
    private String riskLevel;
}
