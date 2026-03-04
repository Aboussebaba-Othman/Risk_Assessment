package com.riskassessment.report.repository;

import com.riskassessment.report.entity.Report;
import com.riskassessment.report.entity.enums.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    List<Report> findByCompanyId(Long companyId);

    List<Report> findByTenantId(Long tenantId);

    List<Report> findByStatus(ReportStatus status);

    List<Report> findByTenantIdAndStatus(Long tenantId, ReportStatus status);
}
