package com.riskassessment.company.controller;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.dto.FinancialDataDto;
import com.riskassessment.company.service.CompanyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyDto> createCompany(@Valid @RequestBody CompanyDto dto) {
        log.info("POST /companies - creating company: {}", dto.getName());
        return ResponseEntity.ok(companyService.createCompany(dto));
    }

    @GetMapping
    public ResponseEntity<List<CompanyDto>> getAllCompanies() {
        return ResponseEntity.ok(companyService.getAllCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyDto> getCompany(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getCompanyById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyDto> updateCompany(@PathVariable Long id, @Valid @RequestBody CompanyDto dto) {
        return ResponseEntity.ok(companyService.updateCompany(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/financials")
    public ResponseEntity<FinancialDataDto> addFinancialData(
            @PathVariable Long id,
            @Valid @RequestBody FinancialDataDto dto) {
        log.info("POST /companies/{}/financials", id);
        return ResponseEntity.ok(companyService.addFinancialData(id, dto));
    }

    @GetMapping("/{id}/financials")
    public ResponseEntity<List<FinancialDataDto>> getFinancialData(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getFinancialData(id));
    }

    @GetMapping("/{id}/financials/latest")
    public ResponseEntity<FinancialDataDto> getLatestFinancialData(@PathVariable Long id) {
        List<FinancialDataDto> financials = companyService.getFinancialData(id);
        if (financials.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(financials.get(0));
    }
}
