package com.riskassessment.analysis.messaging;

import com.riskassessment.analysis.event.ScoreCalculatedEvent;
import com.riskassessment.analysis.service.SwotAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class ScoreEventConsumer {

    private final SwotAnalysisService swotAnalysisService;

    @RabbitListener(queues = RabbitMQConfig.ANALYSIS_QUEUE)
    public void handleScoreCalculated(ScoreCalculatedEvent event) {
        log.info("Received ScoreCalculatedEvent: companyId={} score={} riskLevel={}",
                event.getCompanyId(), event.getOverallScore(), event.getRiskLevel());
        try {
            swotAnalysisService.performSwotAnalysis(
                    event.getCompanyId(),
                    event.getOverallScore(),
                    event.getRiskLevel());
            log.info("Automated SWOT completed for companyId={}", event.getCompanyId());
        } catch (Exception e) {
            log.error("Failed to complete SWOT for companyId={}: {}", event.getCompanyId(), e.getMessage(), e);
        }
    }
}
