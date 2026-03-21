package com.riskassessment.alertservice.controller;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/trigger")
    public ResponseEntity<AlertResponseDTO> triggerAlert(@RequestBody @Valid AlertRequestDTO request) {
        return ResponseEntity.ok(alertService.createAlertFromRequest(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponseDTO> getById(@PathVariable Long id) {
        log.info("Fetching alert by id={}", id);
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AlertResponseDTO>> getByCompany(@PathVariable Long companyId) {
        log.info("Fetching alerts for companyId={}", companyId);
        return ResponseEntity.ok(alertService.getAlertsByCompany(companyId));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAll() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }
}
