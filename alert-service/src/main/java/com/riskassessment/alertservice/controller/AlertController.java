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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.riskassessment.alertservice.service.AlertSseService;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/alerts")
@RequiredArgsConstructor
@Slf4j
public class AlertController {

    private final AlertService alertService;
    private final AlertSseService sseService;

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

    @GetMapping(value = "/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAlerts(Authentication authentication) {
        // Use user's ID/username to register their SSE emitter
        String clientId = authentication != null ? authentication.getName() : "anonymous";
        log.info("SSE subscription request from user: {}", clientId);
        return sseService.subscribe(clientId);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        alertService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }
}
