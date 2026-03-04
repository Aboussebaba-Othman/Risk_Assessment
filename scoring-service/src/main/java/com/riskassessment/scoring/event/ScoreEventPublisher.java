package com.riskassessment.scoring.event;

import com.riskassessment.scoring.config.RabbitMQConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publish(ScoreCalculatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.SCORE_EXCHANGE,
                    RabbitMQConfig.SCORE_ROUTING_KEY,
                    event);
            log.info("Published ScoreCalculatedEvent for companyId={} score={} riskLevel={}",
                    event.getCompanyId(), event.getOverallScore(), event.getRiskLevel());
        } catch (Exception e) {
            log.error("Failed to publish ScoreCalculatedEvent for companyId={}", event.getCompanyId(), e);
        }
    }
}
