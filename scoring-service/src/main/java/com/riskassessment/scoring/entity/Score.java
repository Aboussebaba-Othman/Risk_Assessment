package com.riskassessment.scoring.entity;

import com.riskassessment.scoring.enums.RiskLevel;
import com.riskassessment.scoring.enums.RiskRating;
import com.riskassessment.scoring.enums.ScoringMethod;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main score entity for company risk assessment
 * Contains overall score and detailed breakdowns
 */
@Entity
@Table(name = "scores")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Column(name = "overall_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal overallScore;

    @Column(name = "financial_score", precision = 5, scale = 2)
    private BigDecimal financialScore;

    @Column(name = "operational_score", precision = 5, scale = 2)
    private BigDecimal operationalScore;

    @Column(name = "market_score", precision = 5, scale = 2)
    private BigDecimal marketScore;

    @Column(name = "legal_score", precision = 5, scale = 2)
    private BigDecimal legalScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_rating", nullable = false, length = 20)
    private RiskRating riskRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 20)
    private RiskLevel riskLevel;

    @Column(name = "confidence_level", precision = 5, scale = 2)
    private BigDecimal confidenceLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "scoring_method", nullable = false, length = 50)
    private ScoringMethod scoringMethod = ScoringMethod.AUTOMATED;

    @Column(name = "scored_by")
    private Long scoredBy;

    @Column(name = "scored_at", nullable = false)
    private LocalDateTime scoredAt;

    @Column(name = "valid_until")
    private LocalDateTime validUntil;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "score", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScoreDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (scoredAt == null) {
            scoredAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
