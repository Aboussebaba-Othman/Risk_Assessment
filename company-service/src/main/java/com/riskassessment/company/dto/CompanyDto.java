package com.riskassessment.company.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDto {
    private Long id;
    private Long tenantId;
    private String name;
    private String registrationNumber; // RC (Registre de Commerce)
    private String taxId; // ICE (Identifiant Commun de l'Entreprise)
    private String legalForm; // Forme juridique (SARL, SA, etc.)

    @JsonAlias({ "industry", "industrySector" })
    private String industry;

    private String industrySector;

    private LocalDate incorporationDate; // Date de création (pour ajustement ancienneté CDC)
    private BigDecimal shareCapital; // Capital social

    private String country;
    private String city;
    private String address;
    private String phone;

    @JsonAlias({ "email", "contactEmail" })
    private String email;

    private String contactEmail;
    private String website;
    private Integer employeeCount;
    private BigDecimal annualRevenue;
    private String currency;
    private String status;
    private String riskLevel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public String getIndustrySector() {
        return industrySector != null ? industrySector : industry;
    }

    public String getContactEmail() {
        return contactEmail != null ? contactEmail : email;
    }
}
