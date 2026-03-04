package com.riskassessment.alertservice.controller;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;

    // Manually trigger an alert (used by legacy Feign clients)
    @PostMapping("/trigger")
    public ResponseEntity<Alert> triggerAlert(@RequestBody AlertRequestDTO request) {
        Alert alert = alertService.createAndSendAlert(
                null,
                request.getRecipient(),
                request.getSubject(),
                request.getMessage(),
                request.getType(),
                Alert.AlertSeverity.WARNING);
        return ResponseEntity.ok(alert);
    }

    // Get all alerts for a specific company
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<Alert>> getByCompany(@PathVariable Long companyId) {
        log.info("Fetching alerts for companyId={}", companyId);
        return ResponseEntity.ok(alertService.getAlertsByCompany(companyId));
    }

    // Get all alerts (admin view)
    @GetMapping
    public ResponseEntity<List<Alert>> getAll() {
        return ResponseEntity.ok(alertService.getAllAlerts());
    }
}
