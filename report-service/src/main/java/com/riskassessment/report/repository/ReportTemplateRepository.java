package com.riskassessment.report.repository;

import com.riskassessment.report.entity.ReportTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportTemplateRepository extends MongoRepository<ReportTemplate, String> {
    List<ReportTemplate> findByIsActiveTrue();

    List<ReportTemplate> findByTemplateType(String templateType);
}
