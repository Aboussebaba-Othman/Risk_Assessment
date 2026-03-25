package com.riskassessment.alertservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertStatus;
import com.riskassessment.alertservice.enums.AlertType;
import com.riskassessment.alertservice.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.cloud.config.enabled=false",
        "spring.cloud.config.import-check.enabled=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlertControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertRepository alertRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        alertRepository.deleteAll();
    }

    @Test
    void createAlert_Authorized_ReturnsCreated() throws Exception {
        AlertRequestDTO request = new AlertRequestDTO();
        request.setRecipient("user@test.com");
        request.setSubject("Test Subject");
        request.setMessage("Test Message");
        request.setType(AlertType.SYSTEM_ALERT);

        mockMvc.perform(post("/api/v1/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(jwt().jwt(jwt -> jwt.claim("sub", "99"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.recipient", is("user@test.com")));
    }

    @Test
    void getAlertById_NotFound_ShouldReturn404() throws Exception {
        mockMvc.perform(get("/api/v1/alerts/999").with(jwt()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllAlerts_Authorized_ReturnsList() throws Exception {
        Alert alert = Alert.builder()
                .recipient("a@b.com")
                .subject("Subj")
                .message("Msg")
                .type(AlertType.SCORE_CHANGE)
                .severity(AlertSeverity.WARNING)
                .status(AlertStatus.PENDING)
                .build();
        alertRepository.save(alert);

        mockMvc.perform(get("/api/v1/alerts").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].recipient", is("a@b.com")));
    }
}
