package com.riskassessment.company.repository;

import com.riskassessment.company.entity.CompanyContact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyContactRepository extends JpaRepository<CompanyContact, Long> {

    List<CompanyContact> findByCompanyId(Long companyId);

    List<CompanyContact> findByCompanyIdAndIsPrimary(Long companyId, Boolean isPrimary);
}
