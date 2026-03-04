package com.riskassessment.company.service;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.dto.FinancialDataDto;
import com.riskassessment.company.entity.Company;
import com.riskassessment.company.entity.FinancialData;
import com.riskassessment.company.entity.enums.CompanyStatus;
import com.riskassessment.company.repository.CompanyRepository;
import com.riskassessment.company.repository.FinancialDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final FinancialDataRepository financialDataRepository;

    // ── Company CRUD ─────────────────────────────────────────────────────────

    @Transactional
    public CompanyDto createCompany(CompanyDto dto) {
        // Uniqueness check on registrationNumber (SIRET)
        if (dto.getRegistrationNumber() != null) {
            companyRepository.findByRegistrationNumber(dto.getRegistrationNumber()).ifPresent(existing -> {
                throw new RuntimeException(
                        "A company with registrationNumber '" + dto.getRegistrationNumber() + "' already exists (id="
                                + existing.getId() + ")");
            });
        }

        Company company = new Company();
        company.setTenantId(dto.getTenantId() != null ? dto.getTenantId() : 1L);
        company.setName(dto.getName());
        company.setRegistrationNumber(dto.getRegistrationNumber());
        company.setTaxId(dto.getTaxId());
        company.setIndustry(dto.getIndustry() != null ? dto.getIndustry() : dto.getIndustrySector());
        company.setCountry(dto.getCountry());
        company.setCity(dto.getCity());
        company.setAddress(dto.getAddress());
        company.setPhone(dto.getPhone());
        company.setEmail(dto.getEmail() != null ? dto.getEmail() : dto.getContactEmail());
        company.setWebsite(dto.getWebsite());
        company.setEmployeeCount(dto.getEmployeeCount());
        company.setStatus(CompanyStatus.ACTIVE);
        company.setCurrency("EUR");

        Company saved = companyRepository.save(company);
        log.info("Created company id={} registrationNumber={}", saved.getId(), saved.getRegistrationNumber());
        return toDto(saved);
    }

    public List<CompanyDto> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public CompanyDto getCompanyById(Long id) {
        return companyRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));
    }

    @Transactional
    public CompanyDto updateCompany(Long id, CompanyDto dto) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Company not found: " + id));
        if (dto.getName() != null)
            company.setName(dto.getName());
        if (dto.getTaxId() != null)
            company.setTaxId(dto.getTaxId());
        if (dto.getIndustry() != null)
            company.setIndustry(dto.getIndustry());
        if (dto.getAddress() != null)
            company.setAddress(dto.getAddress());
        if (dto.getEmail() != null)
            company.setEmail(dto.getEmail());
        if (dto.getPhone() != null)
            company.setPhone(dto.getPhone());
        if (dto.getWebsite() != null)
            company.setWebsite(dto.getWebsite());
        if (dto.getEmployeeCount() != null)
            company.setEmployeeCount(dto.getEmployeeCount());
        if (dto.getCity() != null)
            company.setCity(dto.getCity());
        if (dto.getCountry() != null)
            company.setCountry(dto.getCountry());
        return toDto(companyRepository.save(company));
    }

    @Transactional
    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    // ── Financial Data ────────────────────────────────────────────────────────

    @Transactional
    public FinancialDataDto addFinancialData(Long companyId, FinancialDataDto dto) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found: " + companyId));

        FinancialData fd = new FinancialData();
        fd.setCompanyId(companyId);
        fd.setFiscalYear(dto.getFiscalYear() != null ? dto.getFiscalYear() : 2024);
        fd.setPeriodEndDate(dto.getPeriodEndDate() != null
                ? LocalDate.parse(dto.getPeriodEndDate())
                : LocalDate.of(dto.getFiscalYear() != null ? dto.getFiscalYear() : 2024, 12, 31));

        if (dto.getRevenue() != null)
            fd.setRevenue(dto.getRevenue());
        if (dto.getNetResult() != null)
            fd.setNetIncome(dto.getNetResult());
        if (dto.getOperatingIncome() != null)
            fd.setOperatingIncome(dto.getOperatingIncome());
        if (dto.getEbitda() != null)
            fd.setEbitda(dto.getEbitda());
        if (dto.getEquity() != null)
            fd.setEquity(dto.getEquity());
        if (dto.getLongTermDebt() != null)
            fd.setLongTermDebt(dto.getLongTermDebt());
        if (dto.getCurrentAssets() != null)
            fd.setCurrentAssets(dto.getCurrentAssets());
        if (dto.getCurrentLiabilities() != null)
            fd.setCurrentLiabilities(dto.getCurrentLiabilities());
        if (dto.getInventory() != null)
            fd.setInventory(dto.getInventory());
        if (dto.getAccountsReceivable() != null)
            fd.setAccountsReceivable(dto.getAccountsReceivable());
        if (dto.getCash() != null)
            fd.setCash(dto.getCash());
        if (dto.getPaymentIncidents() != null)
            fd.setPaymentIncidents(dto.getPaymentIncidents());
        if (dto.getAveragePaymentDelay() != null)
            fd.setAveragePaymentDelay(dto.getAveragePaymentDelay());

        // ── Previously unmapped fields ────────────────────────────────────────
        if (dto.getAccountsPayable() != null)
            fd.setAccountsPayable(dto.getAccountsPayable());
        if (dto.getTotalAssets() != null)
            fd.setTotalAssets(dto.getTotalAssets());
        if (dto.getFixedAssets() != null)
            fd.setFixedAssets(dto.getFixedAssets());
        if (dto.getTotalLiabilities() != null)
            fd.setTotalLiabilities(dto.getTotalLiabilities());
        if (dto.getFinancialExpenses() != null)
            fd.setFinancialExpenses(dto.getFinancialExpenses());
        if (dto.getTax() != null)
            fd.setTax(dto.getTax());
        if (dto.getDepreciation() != null)
            fd.setDepreciation(dto.getDepreciation());
        if (dto.getAmortization() != null)
            fd.setAmortization(dto.getAmortization());

        FinancialData saved = financialDataRepository.save(fd);
        log.info("Added financial data for company {} (year {})", companyId, dto.getFiscalYear());
        return toFinancialDto(saved);
    }

    public List<FinancialDataDto> getFinancialData(Long companyId) {
        return financialDataRepository.findByCompanyId(companyId).stream()
                .map(this::toFinancialDto)
                .collect(Collectors.toList());
    }

    // ── Mappers ───────────────────────────────────────────────────────────────

    private CompanyDto toDto(Company c) {
        return CompanyDto.builder()
                .id(c.getId())
                .tenantId(c.getTenantId())
                .name(c.getName())
                .registrationNumber(c.getRegistrationNumber())
                .taxId(c.getTaxId())
                .industry(c.getIndustry())
                .country(c.getCountry())
                .city(c.getCity())
                .address(c.getAddress())
                .phone(c.getPhone())
                .email(c.getEmail())
                .website(c.getWebsite())
                .employeeCount(c.getEmployeeCount())
                .annualRevenue(c.getAnnualRevenue())
                .currency(c.getCurrency())
                .status(c.getStatus() != null ? c.getStatus().name() : "ACTIVE")
                .riskLevel(c.getRiskLevel() != null ? c.getRiskLevel().name() : null)
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }

    private FinancialDataDto toFinancialDto(FinancialData fd) {
        return FinancialDataDto.builder()
                .id(fd.getId())
                .companyId(fd.getCompanyId())
                .fiscalYear(fd.getFiscalYear())
                .revenue(fd.getRevenue())
                .netResult(fd.getNetIncome())
                .operatingIncome(fd.getOperatingIncome())
                .ebitda(fd.getEbitda())
                .equity(fd.getEquity())
                .longTermDebt(fd.getLongTermDebt())
                .currentAssets(fd.getCurrentAssets())
                .currentLiabilities(fd.getCurrentLiabilities())
                .inventory(fd.getInventory())
                .accountsReceivable(fd.getAccountsReceivable())
                .accountsPayable(fd.getAccountsPayable())
                .cash(fd.getCash())
                .totalAssets(fd.getTotalAssets())
                .fixedAssets(fd.getFixedAssets())
                .totalLiabilities(fd.getTotalLiabilities())
                .financialExpenses(fd.getFinancialExpenses())
                .tax(fd.getTax())
                .depreciation(fd.getDepreciation())
                .amortization(fd.getAmortization())
                .paymentIncidents(fd.getPaymentIncidents())
                .averagePaymentDelay(fd.getAveragePaymentDelay())
                .build();
    }
}
