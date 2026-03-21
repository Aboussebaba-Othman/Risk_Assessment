package com.riskassessment.analysis.repository;

import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.enums.ResultType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {

    List<AnalysisResult> findByAnalysisId(Long analysisId);

    List<AnalysisResult> findByAnalysisIdAndResultType(Long analysisId, ResultType resultType);
}
