package com.riskassessment.company.service.impl;

import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.entity.Company;
import com.riskassessment.company.exception.DuplicateResourceException;
import com.riskassessment.company.exception.ResourceNotFoundException;
import com.riskassessment.company.mapper.CompanyMapper;
import com.riskassessment.company.repository.CompanyRepository;
import com.riskassessment.company.security.SecurityUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompanyServiceImplTest {

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private CompanyMapper companyMapper;

    @InjectMocks
    private CompanyServiceImpl companyService;

    private MockedStatic<SecurityUtils> mockedSecurityUtils;

    @BeforeEach
    void setUp() {
        mockedSecurityUtils = mockStatic(SecurityUtils.class);
    }

    @AfterEach
    void tearDown() {
        mockedSecurityUtils.close();
    }

    @Test
    void createCompany_Success() {
        // Arrange
        CompanyDto dto = new CompanyDto();
        dto.setName("Test Corp");
        dto.setRegistrationNumber("REG-123");
        dto.setStatus("ACTIVE");

        Company mappedCompany = new Company();
        mappedCompany.setName("Test Corp");
        
        Company savedCompany = new Company();
        savedCompany.setId(1L);
        savedCompany.setName("Test Corp");
        savedCompany.setRegistrationNumber("REG-123");
        savedCompany.setTenantId(100L);

        when(companyRepository.findByRegistrationNumber("REG-123")).thenReturn(Optional.empty());
        when(companyMapper.toEntity(dto)).thenReturn(mappedCompany);
        mockedSecurityUtils.when(SecurityUtils::getTenantId).thenReturn(100L);
        when(companyRepository.save(any(Company.class))).thenReturn(savedCompany);
        
        CompanyDto responseDto = new CompanyDto();
        responseDto.setId(1L);
        responseDto.setTenantId(100L);
        when(companyMapper.toDto(savedCompany)).thenReturn(responseDto);

        // Act
        CompanyDto result = companyService.createCompany(dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(100L, result.getTenantId());
        
        verify(companyRepository).findByRegistrationNumber("REG-123");
        verify(companyRepository).save(any(Company.class));
    }

    @Test
    void createCompany_DuplicateRegistration_ThrowsException() {
        // Arrange
        CompanyDto dto = new CompanyDto();
        dto.setRegistrationNumber("REG-123");

        when(companyRepository.findByRegistrationNumber("REG-123")).thenReturn(Optional.of(new Company()));

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> companyService.createCompany(dto));
        verify(companyRepository, never()).save(any(Company.class));
    }

    @Test
    void getCompanyById_Found_ReturnsDto() {
        // Arrange
        Company company = new Company();
        company.setId(1L);
        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));
        
        CompanyDto companyDto = new CompanyDto();
        companyDto.setId(1L);
        when(companyMapper.toDto(company)).thenReturn(companyDto);

        // Act
        CompanyDto result = companyService.getCompanyById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCompanyById_NotFound_ThrowsException() {
        // Arrange
        when(companyRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> companyService.getCompanyById(99L));
    }
}
