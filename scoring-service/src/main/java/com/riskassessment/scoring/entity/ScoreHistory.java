package com.riskassessment.scoring.entity;

import com.riskassessment.scoring.entity.enums.RiskLevel;
import com.riskassessment.scoring.entity.enums.RiskRating;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Historical record of score changes for a company
 * Tracks evolution of risk assessment over time
 */
@Entity
@Table(name = "score_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "score_id", nullable = false)
    private Long scoreId;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_rating", nullable = false, length = 20)
    private RiskRating riskRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "change_reason", length = 255)
    private String changeReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
