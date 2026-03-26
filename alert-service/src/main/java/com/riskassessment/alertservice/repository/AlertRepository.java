package com.riskassessment.alertservice.repository;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatusOrderByCreatedAtAsc(AlertStatus status);

    List<Alert> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Alert> findAllByOrderByCreatedAtDesc();

    long countByTenantIdAndIsReadFalse(Long tenantId);
}
