package com.riskassessment.analysis.mapper;

import com.riskassessment.analysis.entity.AnalysisResult;
import com.riskassessment.analysis.entity.FinancialAnalysis;
import com.riskassessment.analysis.dto.AnalysisResultDTO;
import com.riskassessment.analysis.dto.FinancialAnalysisDTO;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AnalysisMapper {
    AnalysisResultDTO toDto(AnalysisResult result);
    List<AnalysisResultDTO> toResultDtoList(List<AnalysisResult> results);

    FinancialAnalysisDTO toDto(FinancialAnalysis analysis);
    List<FinancialAnalysisDTO> toDtoList(List<FinancialAnalysis> analyses);
}
