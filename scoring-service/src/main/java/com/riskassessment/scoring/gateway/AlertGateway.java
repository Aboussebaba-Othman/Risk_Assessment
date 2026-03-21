package com.riskassessment.scoring.gateway;

import com.riskassessment.scoring.client.AlertClient;
import com.riskassessment.scoring.dto.AlertRequestDTO;
import com.riskassessment.scoring.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertGateway {

    private final AlertClient alertClient;

    public void triggerAlert(AlertRequestDTO request) {
        try {
            alertClient.triggerAlert(request);
            log.info("Risk alert systematically dispatched to the notification engine via gateway");
        } catch (Exception e) {
            log.error("Failed to propagate automated high-urgency alert to alert-service: {}", e.getMessage());
            throw new ExternalServiceException("Cannot dispatch communication alert", e);
        }
    }
}
