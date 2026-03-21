package com.riskassessment.company.mapper;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.dto.FinancialDataDto;
import com.riskassessment.company.entity.Company;
import com.riskassessment.company.entity.FinancialData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.BeanMapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDate.class})
public interface CompanyMapper {

    @Mapping(target = "industrySector", source = "industry")
    @Mapping(target = "contactEmail", source = "email")
    @Mapping(target = "annualRevenue", source = "annualRevenue")
    CompanyDto toDto(Company company);

    @Mapping(target = "periodEndDate", expression = "java(financialData.getPeriodEndDate() != null ? financialData.getPeriodEndDate().toString() : null)")
    @Mapping(target = "netResult", source = "netIncome")
    FinancialDataDto toFinancialDto(FinancialData financialData);

    @Mapping(target = "industry", source = "industrySector")
    @Mapping(target = "email", source = "contactEmail")
    Company toEntity(CompanyDto companyDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "netIncome", source = "netResult")
    @Mapping(target = "periodEndDate", expression = "java(dto.getPeriodEndDate() != null ? LocalDate.parse(dto.getPeriodEndDate()) : null)")
    void updateFinancialDataFromDto(FinancialDataDto dto, @MappingTarget FinancialData financialData);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "industry", source = "industrySector")
    @Mapping(target = "email", source = "contactEmail")
    void updateCompanyFromDto(CompanyDto dto, @MappingTarget Company company);

    List<CompanyDto> toDtoList(List<Company> companies);
    List<FinancialDataDto> toFinancialDtoList(List<FinancialData> financialDataList);
}
