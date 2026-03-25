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

    // Keep track of emitters by user ID or username. Here we use a general structure.
    // If the system broadcasts to everyone, a simple list works. But since alerts might be per-company or per-user,
    // let's use a concurrent map by string identifier (e.g. username from JWT).
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(String clientId) {
        // Keep connection open for 1 hour or infinite (0)
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

        // Send an initial event to establish connection successfully
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
        log.info("Broadcasting alert {} to {} connected clients", alert.getId(), emitters.size());
        
        emitters.forEach((clientId, emitter) -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("NEW_ALERT")
                        .data(alert));
            } catch (IOException e) {
                log.error("Error dispatching alert to client {}", clientId, e);
                emitters.remove(clientId);
            }
        });
    }
}
