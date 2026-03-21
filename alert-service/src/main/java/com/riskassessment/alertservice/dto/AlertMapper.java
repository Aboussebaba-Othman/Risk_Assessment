package com.riskassessment.alertservice.dto;

import com.riskassessment.alertservice.entity.Alert;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AlertMapper {
    AlertResponseDTO toDto(Alert alert);
    List<AlertResponseDTO> toDtoList(List<Alert> alerts);
}
