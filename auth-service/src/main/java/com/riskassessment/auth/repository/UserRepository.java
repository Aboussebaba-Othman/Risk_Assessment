package com.riskassessment.auth.repository;

import com.riskassessment.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByTenantIdAndEmail(Long tenantId, String email);
    
    boolean existsByEmail(String email);
    
    boolean existsByTenantIdAndEmail(Long tenantId, String email);
    
    long countByTenantId(Long tenantId);
}

