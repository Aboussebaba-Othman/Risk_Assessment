package com.riskassessment.alertservice.service;

import com.riskassessment.alertservice.dto.AlertResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class AlertSseService {

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String clientId) {
        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(clientId, emitter);

        emitter.onCompletion(() -> {
            log.debug("SSE completion for client {}", clientId);
            emitters.remove(clientId);
        });

        emitter.onTimeout(() -> {
            log.debug("SSE timeout for client {}", clientId);
            emitters.remove(clientId);
        });

        emitter.onError(e -> {
            log.error("SSE error for client {}: {}", clientId, e.getMessage());
            emitters.remove(clientId);
        });

        try {
            emitter.send(SseEmitter.event().name("INIT").data("Connected"));
            log.info("SSE client subscribed: {}", clientId);
        } catch (IOException e) {
            log.error("Error sending INIT event to client {}", clientId, e);
            emitters.remove(clientId);
        }

        return emitter;
    }

    public void dispatch(AlertResponseDTO alert) {
        log.info("Dispatching alert {} (tenantId={}) to {} connected clients", alert.getId(), alert.getTenantId(), emitters.size());
        
        emitters.forEach((clientId, emitter) -> {
            if (alert.getTenantId() == null || clientId.equals(alert.getTenantId().toString())) {
                log.info("SSE: Match found. Sending alert {} to client {} (target tenantId={})", 
                        alert.getId(), clientId, alert.getTenantId());
                try {
                    emitter.send(SseEmitter.event()
                            .name("NEW_ALERT")
                            .data(alert));
                    log.debug("Sent alert {} to client {}", alert.getId(), clientId);
                } catch (IOException e) {
                    log.error("Error dispatching alert to client {}", clientId, e);
                    emitters.remove(clientId);
                }
            }
        });
    }
}
