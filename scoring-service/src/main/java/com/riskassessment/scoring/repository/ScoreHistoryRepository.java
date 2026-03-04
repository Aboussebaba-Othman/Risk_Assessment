package com.riskassessment.scoring.repository;

import com.riskassessment.scoring.entity.ScoreHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScoreHistoryRepository extends JpaRepository<ScoreHistory, Long> {

    List<ScoreHistory> findByCompanyId(Long companyId);

    List<ScoreHistory> findByScoreId(Long scoreId);

    List<ScoreHistory> findByCompanyIdOrderByCreatedAtDesc(Long companyId);
}
