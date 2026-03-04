package com.riskassessment.analysis.repository;

import com.riskassessment.analysis.entity.AnalysisAttachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalysisAttachmentRepository extends JpaRepository<AnalysisAttachment, Long> {

    List<AnalysisAttachment> findByAnalysisId(Long analysisId);
}
