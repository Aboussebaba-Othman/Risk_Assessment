package com.riskassessment.analysis.controller;

import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.service.SwotAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/analysis")
@RequiredArgsConstructor
public class AnalysisController {

    private final SwotAnalysisService swotAnalysisService;

    @PostMapping("/swot/{companyId}")
    public ResponseEntity<AnalysisResult> performSwot(@PathVariable Long companyId) {
        return ResponseEntity.ok(swotAnalysisService.performSwotAnalysis(companyId));
    }
}
