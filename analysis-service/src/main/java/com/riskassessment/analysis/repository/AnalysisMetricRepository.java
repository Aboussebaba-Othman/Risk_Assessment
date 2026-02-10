package com.riskassessment.analysis.repository;

import com.riskassessment.analysis.entity.AnalysisMetric;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisMetricRepository extends JpaRepository<AnalysisMetric, Long> {

    List<AnalysisMetric> findByAnalysisId(Long analysisId);
}
