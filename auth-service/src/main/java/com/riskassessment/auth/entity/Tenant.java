package com.riskassessment.auth.entity;

import com.riskassessment.auth.entity.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "subscription_plan", nullable = false, length = 20)
    private SubscriptionPlan subscriptionPlan = SubscriptionPlan.STARTER;

    @Column(name = "subscription_start_date")
    private LocalDateTime subscriptionStartDate;

    @Column(name = "subscription_end_date")
    private LocalDateTime subscriptionEndDate;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "max_users")
    private Integer maxUsers = 5;

    @Column(name = "max_companies")
    private Integer maxCompanies = 100;

    @Column(name = "max_analyses_per_month")
    private Integer maxAnalysesPerMonth = 50;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
