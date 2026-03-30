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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            Long currentTenantId = SecurityUtils.getTenantId();
            company.setTenantId(currentTenantId != null ? currentTenantId : 1L);
        }
        company.setCurrency("MAD");

        try {
            company.setStatus(dto.getStatus() != null ? CompanyStatus.valueOf(dto.getStatus()) : CompanyStatus.ACTIVE);
        } catch (IllegalArgumentException ignored) {
            company.setStatus(CompanyStatus.ACTIVE);
        }

        Company saved = companyRepository.save(company);
        log.info("Created company id={} registrationNumber={} tenantId={}", saved.getId(),
                saved.getRegistrationNumber(), saved.getTenantId());
        return companyMapper.toDto(saved);
    }

    @Override
    public List<CompanyDto> getAllCompanies() {
        Long tenantId = SecurityUtils.getTenantId();
        log.info("DEBUG: Extracted tenant_id from JWT in CompanyService: {}", tenantId);
        if (tenantId == null)
            return Collections.emptyList();

        List<CompanyDto> companies = companyRepository.findByTenantId(tenantId).stream()
                .map(companyMapper::toDto)
                .collect(Collectors.toList());
        log.info("DEBUG: Found {} companies for tenantId {}", companies.size(), tenantId);
        return companies;
    }

    @Override
    public CompanyDto getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + id));
        Long currentTenantId = SecurityUtils.getTenantId();
        if (currentTenantId != null && !currentTenantId.equals(company.getTenantId())) {
            log.warn("Tenant isolation: tenantId={} tried to access company {} owned by tenantId={}",
                    currentTenantId, id, company.getTenantId());
            throw new ResourceNotFoundException("Company not found: " + id);
        }
        return companyMapper.toDto(company);
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

        companyMapper.updateFinancialDataFromDto(dto, fd);

        fd.setCompanyId(companyId);
        fd.setFiscalYear(targetYear);

        if (fd.getPeriodEndDate() == null) {
            fd.setPeriodEndDate(LocalDate.of(targetYear, 12, 31));
        }

        FinancialData saved = financialDataRepository.save(fd);
        log.info("Added financial data for company {} (year {})", companyId, dto.getFiscalYear());
        return companyMapper.toFinancialDto(saved);
    }

    @Override
    public List<FinancialDataDto> getFinancialData(Long companyId) {
        return companyMapper
                .toFinancialDtoList(financialDataRepository.findByCompanyIdOrderByFiscalYearDesc(companyId));
    }


}
