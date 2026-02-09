package com.riskassessment.company.repository;

import com.riskassessment.company.entity.Company;
import com.riskassessment.company.entity.enums.CompanyStatus;
import com.riskassessment.company.entity.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    List<Company> findByTenantId(Long tenantId);

    Optional<Company> findByRegistrationNumber(String registrationNumber);

    List<Company> findByTenantIdAndStatus(Long tenantId, CompanyStatus status);

    List<Company> findByTenantIdAndRiskLevel(Long tenantId, RiskLevel riskLevel);

    boolean existsByRegistrationNumber(String registrationNumber);
}
