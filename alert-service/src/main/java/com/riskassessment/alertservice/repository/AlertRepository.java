package com.riskassessment.alertservice.repository;

import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByStatusOrderByCreatedAtAsc(AlertStatus status);

    List<Alert> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Alert> findByTenantIdOrderByCreatedAtDesc(Long tenantId);

    List<Alert> findAllByOrderByCreatedAtDesc();

    long countByTenantIdAndIsReadFalse(Long tenantId);

    @org.springframework.data.jpa.repository.Modifying
    @org.springframework.data.jpa.repository.Query("UPDATE Alert a SET a.isRead = true, a.readAt = CURRENT_TIMESTAMP WHERE a.tenantId = :tenantId AND a.isRead = false")
    void markAllAsReadByTenantId(Long tenantId);
}
