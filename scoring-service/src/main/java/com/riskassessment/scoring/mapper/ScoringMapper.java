package com.riskassessment.scoring.mapper;

import com.riskassessment.scoring.dto.CompanyFinancialsDTO;
import com.riskassessment.scoring.dto.FinancialDataDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScoringMapper {

 
    FinancialDataDTO toFinancialDataDTO(CompanyFinancialsDTO source);
}
