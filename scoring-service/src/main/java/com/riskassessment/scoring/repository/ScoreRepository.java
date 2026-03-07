package com.riskassessment.scoring.repository;

import com.riskassessment.scoring.entity.Score;
import com.riskassessment.scoring.entity.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Long> {

    List<Score> findByCompanyId(Long companyId);

    List<Score> findByTenantId(Long tenantId);

    List<Score> findByTenantIdAndRiskLevel(Long tenantId, RiskLevel riskLevel);

    Optional<Score> findTopByCompanyIdOrderByScoredAtDesc(Long companyId);

    List<Score> findByCompanyIdOrderByScoredAtDesc(Long companyId);
}
