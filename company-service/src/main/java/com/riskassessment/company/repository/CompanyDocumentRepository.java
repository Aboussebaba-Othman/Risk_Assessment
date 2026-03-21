package com.riskassessment.company.repository;

import com.riskassessment.company.entity.CompanyDocument;
import com.riskassessment.company.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyDocumentRepository extends JpaRepository<CompanyDocument, Long> {

    List<CompanyDocument> findByCompanyId(Long companyId);

    List<CompanyDocument> findByCompanyIdAndDocumentType(Long companyId, DocumentType documentType);
}
