package com.riskassessment.report.repository;

import com.riskassessment.report.entity.Report;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByCompanyIdOrderByReportDateDesc(Long companyId);
}
