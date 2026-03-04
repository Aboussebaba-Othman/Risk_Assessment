package com.riskassessment.report.repository;

import com.riskassessment.report.entity.ReportTemplate;
import com.riskassessment.report.entity.enums.TemplateType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportTemplateRepository extends JpaRepository<ReportTemplate, Long> {

    List<ReportTemplate> findByTemplateType(TemplateType templateType);

    List<ReportTemplate> findByIsActiveTrue();
}
