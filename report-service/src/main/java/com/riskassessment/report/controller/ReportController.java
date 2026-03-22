package com.riskassessment.report.controller;

import com.riskassessment.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportGenerationService;

    @GetMapping("/company/{companyId}/download")
    public ResponseEntity<byte[]> downloadCompanyReport(@PathVariable Long companyId) {
        
        byte[] pdfContent = reportGenerationService.generateCompanyReport(companyId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=risk_report_" + companyId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
}
