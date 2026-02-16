package com.riskassessment.alertservice.controller;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @PostMapping("/trigger")
    public ResponseEntity<Alert> triggerAlert(@RequestBody AlertRequestDTO request) {
        Alert alert = alertService.createAndSendAlert(
                request.getRecipient(),
                request.getSubject(),
                request.getMessage(),
                request.getType());
        return ResponseEntity.ok(alert);
    }
}
