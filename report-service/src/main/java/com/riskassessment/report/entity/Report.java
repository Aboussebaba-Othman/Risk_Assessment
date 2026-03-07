package com.riskassessment.report.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

    @Id
    private String id;

    @Field("company_id")
    private Long companyId;

    @Field("report_type")
    private String reportType;

    @Field("format")
    private String format;

    @Field("report_date")
    private LocalDateTime reportDate;

    @Field("status")
    private String status;

    @Field("generated_by")
    private Long generatedBy;
}
