package com.riskassessment.scoring.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Detailed breakdown of score by criteria
 */
@Entity
@Table(name = "score_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScoreDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "score_id", nullable = false)
    private Score score;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "criteria_id", nullable = false)
    private ScoringCriteria criteria;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal value;

    @Column(name = "weighted_value", nullable = false, precision = 10, scale = 2)
    private BigDecimal weightedValue;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
