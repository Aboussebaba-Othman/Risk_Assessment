package com.riskassessment.analysis.entity;

import com.riskassessment.analysis.entity.enums.MetricCategory;
import com.riskassessment.analysis.entity.enums.MetricInterpretation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Detailed financial metric for an analysis
 */
@Entity
@Table(name = "analysis_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "analysis_id", nullable = false)
    private FinancialAnalysis analysis;

    @Column(name = "metric_name", nullable = false, length = 100)
    private String metricName;

    @Enumerated(EnumType.STRING)
    @Column(name = "metric_category", nullable = false, length = 50)
    private MetricCategory metricCategory;

    @Column(name = "metric_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "benchmark_value", precision = 15, scale = 4)
    private BigDecimal benchmarkValue;

    @Column(name = "variance_percentage", precision = 5, scale = 2)
    private BigDecimal variancePercentage;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private MetricInterpretation interpretation;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
