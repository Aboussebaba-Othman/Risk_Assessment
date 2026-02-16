package com.riskassessment.scoring.client;

import com.riskassessment.scoring.dto.AlertRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "alert-service", url = "http://localhost:9006") // Hardcoded URL for MVP, should be via Registry
public interface AlertClient {

    @PostMapping("/api/v1/alerts/trigger")
    void triggerAlert(@RequestBody AlertRequestDTO request);
}
