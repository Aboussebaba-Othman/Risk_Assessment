package com.riskassessment.alertservice.repository;

import com.riskassessment.alertservice.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatus(Alert.AlertStatus status);

    List<Alert> findByStatusIn(List<Alert.AlertStatus> statuses);
}
