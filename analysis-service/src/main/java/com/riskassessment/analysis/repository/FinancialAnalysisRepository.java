package com.riskassessment.analysis.repository;

import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.entity.enums.AnalysisStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinancialAnalysisRepository extends JpaRepository<FinancialAnalysis, Long> {

    List<FinancialAnalysis> findByCompanyId(Long companyId);

    List<FinancialAnalysis> findByTenantId(Long tenantId);

    List<FinancialAnalysis> findByStatus(AnalysisStatus status);

    List<FinancialAnalysis> findByCompanyIdOrderByPeriodEndDesc(Long companyId);
}
