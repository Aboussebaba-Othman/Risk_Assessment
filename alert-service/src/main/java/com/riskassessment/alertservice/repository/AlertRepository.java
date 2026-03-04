package com.riskassessment.alertservice.repository;

import com.riskassessment.alertservice.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatusOrderByCreatedAtAsc(Alert.AlertStatus status);

    List<Alert> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Alert> findAllByOrderByCreatedAtDesc();
}
