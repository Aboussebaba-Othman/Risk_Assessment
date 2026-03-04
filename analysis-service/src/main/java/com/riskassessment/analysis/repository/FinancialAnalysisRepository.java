package com.riskassessment.analysis.repository;

import com.riskassessment.analysis.entity.FinancialAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinancialAnalysisRepository extends JpaRepository<FinancialAnalysis, Long> {
    Optional<FinancialAnalysis> findTopByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<FinancialAnalysis> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
