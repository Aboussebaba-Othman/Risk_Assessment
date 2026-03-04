package com.riskassessment.scoring.repository;

import com.riskassessment.scoring.entity.ScoringCriteria;
import com.riskassessment.scoring.entity.enums.ScoringCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoringCriteriaRepository extends JpaRepository<ScoringCriteria, Long> {

    List<ScoringCriteria> findByCategory(ScoringCategory category);

    List<ScoringCriteria> findByIsActiveTrue();

    List<ScoringCriteria> findByCategoryAndIsActiveTrue(ScoringCategory category, Boolean isActive);
}
