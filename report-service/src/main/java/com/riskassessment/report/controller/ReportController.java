package com.riskassessment.report.controller;

import com.riskassessment.report.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final IReportService reportGenerationService;

    @PreAuthorize("hasRole('ANALYST') or hasRole('ADMIN')")
    @GetMapping(value = "/company/{companyId}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadCompanyReport(@PathVariable Long companyId) {
        
        System.out.println("AUTHORITIES DEBUG: " + org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        byte[] pdfContent = reportGenerationService.generateCompanyReport(companyId);

        System.out.println("AUTHORITIES: " + org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities()); 
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"risk_report_" + companyId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

    @GetMapping(value = "/debug", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> debugAuthorities() {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(java.util.Map.of(
            "authorities", auth.getAuthorities(),
            "name", auth.getName()
        ));
    }
}
