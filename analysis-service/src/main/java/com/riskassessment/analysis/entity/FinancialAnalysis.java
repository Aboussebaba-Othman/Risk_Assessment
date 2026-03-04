package com.riskassessment.analysis.entity;

import com.riskassessment.analysis.entity.enums.AnalysisStatus;
import com.riskassessment.analysis.entity.enums.AnalysisType;
import com.riskassessment.analysis.entity.enums.OverallHealth;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Main financial analysis entity
 */
@Entity
@Table(name = "financial_analyses")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FinancialAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "tenant_id", nullable = false)
    private Long tenantId;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false, length = 50)
    private AnalysisType analysisType;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AnalysisStatus status = AnalysisStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_health", length = 20)
    private OverallHealth overallHealth;

    @Column(precision = 15, scale = 2)
    private BigDecimal revenue;

    @Column(precision = 15, scale = 2)
    private BigDecimal expenses;

    @Column(name = "net_profit", precision = 15, scale = 2)
    private BigDecimal netProfit;

    @Column(precision = 15, scale = 2)
    private BigDecimal assets;

    @Column(precision = 15, scale = 2)
    private BigDecimal liabilities;

    @Column(precision = 15, scale = 2)
    private BigDecimal equity;

    @Column(name = "cash_flow", precision = 15, scale = 2)
    private BigDecimal cashFlow;

    @Column(length = 3)
    private String currency = "USD";

    @Column(name = "analyzed_by")
    private Long analyzedBy;

    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisMetric> metrics = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisResult> results = new ArrayList<>();

    @OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnalysisAttachment> attachments = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
