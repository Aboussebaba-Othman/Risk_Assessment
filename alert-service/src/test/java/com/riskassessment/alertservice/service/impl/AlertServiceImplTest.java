package com.riskassessment.alertservice.service.impl;

import com.riskassessment.alertservice.dto.AlertRequestDTO;
import com.riskassessment.alertservice.dto.AlertResponseDTO;
import com.riskassessment.alertservice.entity.Alert;
import com.riskassessment.alertservice.enums.AlertSeverity;
import com.riskassessment.alertservice.enums.AlertStatus;
import com.riskassessment.alertservice.enums.AlertType;
import com.riskassessment.alertservice.exception.AlertNotFoundException;
import com.riskassessment.alertservice.mapper.AlertMapper;
import com.riskassessment.alertservice.repository.AlertRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertMapper alertMapper;

    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    void createAndSendAlert_FullParams_Success() {
        // Arrange
        Alert savedAlert = new Alert();
        savedAlert.setId(1L);
        savedAlert.setCompanyId(99L);
        savedAlert.setRecipient("test@domain.com");
        
        AlertResponseDTO responseDto = new AlertResponseDTO();
        responseDto.setId(1L);
        responseDto.setCompanyId(99L);

        when(alertRepository.save(any(Alert.class))).thenReturn(savedAlert);
        when(alertMapper.toDto(savedAlert)).thenReturn(responseDto);

        // Act
        AlertResponseDTO result = alertService.createAndSendAlert(99L, "test@domain.com", "Subject", "Msg", AlertType.SCORE_CHANGE, AlertSeverity.HIGH);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(99L, result.getCompanyId());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void getAlertById_Found_ReturnsDto() {
        // Arrange
        Alert alert = new Alert();
        alert.setId(1L);
        
        AlertResponseDTO dto = new AlertResponseDTO();
        dto.setId(1L);
        
        when(alertRepository.findById(1L)).thenReturn(Optional.of(alert));
        when(alertMapper.toDto(alert)).thenReturn(dto);

        // Act
        AlertResponseDTO result = alertService.getAlertById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getAlertById_NotFound_ThrowsException() {
        // Arrange
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(AlertNotFoundException.class, () -> alertService.getAlertById(99L));
    }

    @Test
    void getAlertsByCompany_ReturnsList() {
        // Arrange
        List<Alert> alerts = List.of(new Alert());
        List<AlertResponseDTO> dtos = List.of(new AlertResponseDTO());
        
        when(alertRepository.findByCompanyIdOrderByCreatedAtDesc(99L)).thenReturn(alerts);
        when(alertMapper.toDtoList(alerts)).thenReturn(dtos);

        // Act
        List<AlertResponseDTO> result = alertService.getAlertsByCompany(99L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}
