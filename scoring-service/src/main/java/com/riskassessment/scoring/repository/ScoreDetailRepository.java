package com.riskassessment.scoring.repository;

import com.riskassessment.scoring.entity.ScoreDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreDetailRepository extends JpaRepository<ScoreDetail, Long> {

    List<ScoreDetail> findByScoreId(Long scoreId);

    List<ScoreDetail> findByCriteriaId(Long criteriaId);
}
