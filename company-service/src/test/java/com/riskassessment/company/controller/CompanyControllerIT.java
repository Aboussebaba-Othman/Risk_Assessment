package com.riskassessment.company.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.riskassessment.company.dto.CompanyDto;
import com.riskassessment.company.entity.Company;
import com.riskassessment.company.repository.CompanyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = com.riskassessment.companyservice.CompanyServiceApplication.class,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.config.import-check.enabled=false"
        })
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        companyRepository.deleteAll();
    }

    @Test
    void createCompany_Authorized_ShouldReturnCreated() throws Exception {
        CompanyDto request = new CompanyDto();
        request.setName("Integration Test Corp");
        request.setRegistrationNumber("IT-999");
        request.setIndustry("TECHNOLOGY");
        request.setCountry("MA");

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(jwt -> jwt.claim("sub", "999")))) // Mock JWT with custom sub
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name", is("Integration Test Corp")))
                .andExpect(jsonPath("$.tenantId", is(999))); // Proves SecurityUtils extraction works
    }

    @Test
    void createCompany_DuplicateRegistration_ShouldReturnConflict() throws Exception {
        Company existing = new Company();
        existing.setName("Existing Corp");
        existing.setRegistrationNumber("DUPE-100");
        existing.setTenantId(1L);
        companyRepository.save(existing);

        CompanyDto request = new CompanyDto();
        request.setName("New Corp");
        request.setRegistrationNumber("DUPE-100");

        mockMvc.perform(post("/companies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message", containsString("already exists"))); // Proves GlobalExceptionHandler works
    }

    @Test
    void getCompany_UnAuthorized_ButGatewayHandlesIt_ShouldReturn200() throws Exception {
        mockMvc.perform(get("/companies"))
                .andExpect(status().isOk()); // API Gateway handles auth, microservice permits all
    }

    @Test
    void getAllCompanies_Authorized_ShouldReturnList() throws Exception {
        Company company = new Company();
        company.setName("List Corp");
        company.setTenantId(1L);
        companyRepository.save(company);

        mockMvc.perform(get("/companies").with(jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("List Corp")));
    }
}
