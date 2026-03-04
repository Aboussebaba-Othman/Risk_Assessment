package com.riskassessment.alert.repository;

import com.riskassessment.alert.entity.Alert;
import com.riskassessment.alert.entity.enums.AlertSeverity;
import com.riskassessment.alert.entity.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByTenantId(Long tenantId);

    List<Alert> findByCompanyId(Long companyId);

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findByTenantIdAndStatus(Long tenantId, AlertStatus status);

    List<Alert> findByTenantIdAndSeverity(Long tenantId, AlertSeverity severity);
}
