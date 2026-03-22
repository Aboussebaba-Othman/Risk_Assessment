package com.riskassessment.scoring.gateway;

import com.riskassessment.scoring.client.AlertClient;
import com.riskassessment.scoring.dto.AlertRequestDTO;
import com.riskassessment.scoring.exception.ExternalServiceException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertGateway {

    private final AlertClient alertClient;

    @CircuitBreaker(name = "alert-service", fallbackMethod = "fallbackTriggerAlert")
    public void triggerAlert(AlertRequestDTO request) {
        try {
            alertClient.triggerAlert(request);
        } catch (Exception e) {
            log.warn("Failed to trigger alert for recipient={}: {}", request.getRecipient(), e.getMessage());
            throw new ExternalServiceException("Cannot trigger alert in alert-service", e);
        }
    }

    public void fallbackTriggerAlert(AlertRequestDTO request, Exception ex) {
        log.error("Circuit breaker OPEN for alert-service [triggerAlert] recipient={}: {}", request.getRecipient(), ex.getMessage());
        log.warn("Alert NOT sent due to alert-service unavailability. recipient={}", request.getRecipient());
    }
}
