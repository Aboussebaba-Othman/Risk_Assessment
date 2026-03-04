package com.riskassessment.auth.repository;

import com.riskassessment.auth.entity.Tenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Long> {
    
    Optional<Tenant> findByName(String name);
    
    boolean existsByName(String name);
}
