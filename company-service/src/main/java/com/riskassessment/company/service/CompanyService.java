package com.riskassessment.company.service;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.dto.FinancialDataDto;

import java.util.List;

public interface CompanyService {

    CompanyDto createCompany(CompanyDto dto);

    List<CompanyDto> getAllCompanies();

    CompanyDto getCompanyById(Long id);

    CompanyDto updateCompany(Long id, CompanyDto dto);

    void deleteCompany(Long id);

    FinancialDataDto addFinancialData(Long companyId, FinancialDataDto dto);

    List<FinancialDataDto> getFinancialData(Long companyId);
}
