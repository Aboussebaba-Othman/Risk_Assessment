package com.riskassessment.company.repository;

import com.riskassessment.company.entity.FinancialData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialDataRepository extends JpaRepository<FinancialData, Long> {

    List<FinancialData> findByCompanyId(Long companyId);

    Optional<FinancialData> findByCompanyIdAndFiscalYear(Long companyId, Integer fiscalYear);

    List<FinancialData> findByCompanyIdOrderByFiscalYearDesc(Long companyId);
}
