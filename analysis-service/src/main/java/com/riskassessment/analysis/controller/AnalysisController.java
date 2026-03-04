package com.riskassessment.analysis.controller;

import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.repository.FinancialAnalysisRepository;
import com.riskassessment.analysis.service.SwotAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/analysis")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final SwotAnalysisService swotAnalysisService;
    private final FinancialAnalysisRepository repository;

    /** Manually trigger a new SWOT analysis */
    @PostMapping("/companies/{companyId}/trigger")
    public ResponseEntity<FinancialAnalysis> triggerSwot(@PathVariable Long companyId) {
        log.info("Manual SWOT trigger for companyId={}", companyId);
        return ResponseEntity.ok(swotAnalysisService.performSwotAnalysis(companyId));
    }

    /** Get the latest SWOT analysis for a company */
    @GetMapping("/companies/{companyId}/latest")
    public ResponseEntity<FinancialAnalysis> getLatest(@PathVariable Long companyId) {
        return repository.findTopByCompanyIdOrderByCreatedAtDesc(companyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Get full analysis history for a company */
    @GetMapping("/companies/{companyId}/history")
    public ResponseEntity<List<FinancialAnalysis>> getHistory(@PathVariable Long companyId) {
        return ResponseEntity.ok(repository.findByCompanyIdOrderByCreatedAtDesc(companyId));
    }
}
