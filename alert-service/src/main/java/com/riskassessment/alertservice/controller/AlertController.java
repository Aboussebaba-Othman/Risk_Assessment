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
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

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
    public ResponseEntity<AlertResponseDTO> getById(@PathVariable Long id, Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        log.info("Fetching alert by id={} for tenantId={}", id, tenantId);
        return ResponseEntity.ok(alertService.getAlertById(id, tenantId));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<AlertResponseDTO>> getByCompany(@PathVariable Long companyId, Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        log.info("Fetching alerts for companyId={} for tenantId={}", companyId, tenantId);
        return ResponseEntity.ok(alertService.getAlertsByCompany(companyId, tenantId));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponseDTO>> getAll(Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        return ResponseEntity.ok(alertService.getAllAlerts(tenantId));
    }

    @GetMapping(value = "/stream", produces = org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamAlerts(Authentication authentication) {
        String clientId = getTenantIdFromAuth(authentication);
        log.info("SSE subscription request from user: {} (TenantId: {})", 
                authentication != null ? authentication.getName() : "anonymous", clientId);
        return sseService.subscribe(clientId);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id, Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        alertService.markAsRead(id, tenantId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        return ResponseEntity.ok(alertService.countUnreadAlerts(tenantId));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        Long tenantId = Long.valueOf(getTenantIdFromAuth(authentication));
        alertService.markAllAsRead(tenantId);
        return ResponseEntity.noContent().build();
    }

    private String getTenantIdFromAuth(Authentication authentication) {
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            // Try different common claim names for tenantId
            Object tenantId = jwt.getClaim("tenantId");
            if (tenantId == null) tenantId = jwt.getClaim("tenant_id");
            if (tenantId == null) tenantId = jwt.getClaim("tenant-id");
            
            if (tenantId != null) {
                return tenantId.toString();
            }
        }
        return "1";
    }
}
