package com.riskassessment.scoring.client;

import com.riskassessment.scoring.dto.AlertRequestDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "alert-service")
public interface AlertClient {

    @PostMapping("/alerts/trigger")
    void triggerAlert(@RequestBody AlertRequestDTO request);
}
