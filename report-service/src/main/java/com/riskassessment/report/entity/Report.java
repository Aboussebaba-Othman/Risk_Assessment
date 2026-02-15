package com.riskassessment.report.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @Column(name = "report_type", nullable = false)
    private String reportType; // e.g., FULL_RISK_ASSESSMENT

    @Column(name = "format", nullable = false)
    private String format; // PDF, HTML

    @Column(name = "report_date", nullable = false)
    private LocalDateTime reportDate;

    @Column(name = "status")
    private String status;

    @Column(name = "generated_by")
    private Long generatedBy;
}
