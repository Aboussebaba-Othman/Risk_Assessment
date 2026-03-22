package com.riskassessment.company.service.impl;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.mapper.CompanyMapper;
import com.riskassessment.company.dto.FinancialDataDto;
import com.riskassessment.company.entity.Company;
import com.riskassessment.company.entity.FinancialData;
import com.riskassessment.company.enums.CompanyStatus;
import com.riskassessment.company.exception.DuplicateResourceException;
import com.riskassessment.company.exception.ResourceNotFoundException;
import com.riskassessment.company.repository.CompanyRepository;
import com.riskassessment.company.repository.FinancialDataRepository;
import com.riskassessment.company.security.SecurityUtils;
import com.riskassessment.company.service.CompanyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final FinancialDataRepository financialDataRepository;
    private final CompanyMapper companyMapper;

    @Override
    @Transactional
    public CompanyDto createCompany(CompanyDto dto) {
        if (dto.getRegistrationNumber() != null) {
            companyRepository.findByRegistrationNumber(dto.getRegistrationNumber()).ifPresent(existing -> {
                throw new DuplicateResourceException(
                        "A company with registrationNumber '" + dto.getRegistrationNumber() + "' already exists");
            });
        }

        Company company = companyMapper.toEntity(dto);
        if (company.getTenantId() == null) {
            Long currentUserId = SecurityUtils.getCurrentUserId();
            company.setTenantId(currentUserId != null ? currentUserId : 1L);
        }
        company.setCurrency("MAD");
        
        try {
            company.setStatus(dto.getStatus() != null ? CompanyStatus.valueOf(dto.getStatus()) : CompanyStatus.ACTIVE);
        } catch (IllegalArgumentException ignored) {
            company.setStatus(CompanyStatus.ACTIVE);
        }

        Company saved = companyRepository.save(company);
        log.info("Created company id={} registrationNumber={}", saved.getId(), saved.getRegistrationNumber());
        return companyMapper.toDto(saved);
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        return companyMapper.toDtoList(companyRepository.findAll());
    }

    @Override
    public CompanyDto getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(companyMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
    }

    @Override
    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
        
        companyMapper.updateCompanyFromDto(dto, company);
        
        return companyMapper.toDto(companyRepository.save(company));
    }

    @Override
    @Transactional
    public void deleteCompany(Long id) {
        if (!companyRepository.existsById(id)) {
            throw new ResourceNotFoundException("Company not found: " + id);
        }
        companyRepository.deleteById(id);
    }

    @Override
    @Transactional
    public FinancialDataDto addFinancialData(Long companyId, FinancialDataDto dto) {
        Integer targetYear = dto.getFiscalYear() != null ? dto.getFiscalYear() : 2026;
        FinancialData fd = financialDataRepository.findByCompanyIdAndFiscalYear(companyId, targetYear)
                .orElse(new FinancialData());

        fd.setCompanyId(companyId);
        fd.setFiscalYear(targetYear);
        
        if (fd.getId() == null && dto.getPeriodEndDate() == null) {
            fd.setPeriodEndDate(LocalDate.of(targetYear, 12, 31));
        }

        companyMapper.updateFinancialDataFromDto(dto, fd);

        FinancialData saved = financialDataRepository.save(fd);
        log.info("Added financial data for company {} (year {})", companyId, dto.getFiscalYear());
        return companyMapper.toFinancialDto(saved);
    }

    @Override
    public List<FinancialDataDto> getFinancialData(Long companyId) {
        return companyMapper.toFinancialDtoList(financialDataRepository.findByCompanyIdOrderByFiscalYearDesc(companyId));
    }
}
