package com.riskassessment.analysis.controller;

import com.riskassessment.analysis.mapper.AnalysisMapper;
import com.riskassessment.analysis.dto.FinancialAnalysisDTO;
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
    private final AnalysisMapper analysisMapper;

    @PostMapping("/companies/{companyId}/trigger")
    public ResponseEntity<FinancialAnalysisDTO> triggerSwot(@PathVariable Long companyId) {
        log.info("Manual SWOT trigger for companyId={}", companyId);
        return ResponseEntity.ok(analysisMapper.toDto(swotAnalysisService.performSwotAnalysis(companyId)));
    }

    @GetMapping("/companies/{companyId}/latest")
    public ResponseEntity<FinancialAnalysisDTO> getLatest(@PathVariable Long companyId) {
        return ResponseEntity.ok(analysisMapper.toDto(swotAnalysisService.getLatestAnalysis(companyId)));
    }
    @GetMapping("/companies/{companyId}/history")
    public ResponseEntity<List<FinancialAnalysisDTO>> getHistory(@PathVariable Long companyId) {
        return ResponseEntity.ok(analysisMapper.toDtoList(swotAnalysisService.getAnalysisHistory(companyId)));
    }
}
