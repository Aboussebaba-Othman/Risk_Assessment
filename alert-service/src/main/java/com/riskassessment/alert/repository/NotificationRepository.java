package com.riskassessment.alert.repository;

import com.riskassessment.alert.entity.Notification;
import com.riskassessment.alert.entity.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientId(Long recipientId);

    List<Notification> findByAlertId(Long alertId);

    List<Notification> findByRecipientIdAndStatus(Long recipientId, NotificationStatus status);
}
