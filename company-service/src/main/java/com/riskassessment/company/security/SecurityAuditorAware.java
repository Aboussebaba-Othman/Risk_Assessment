package com.riskassessment.company.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component("auditorAware")
public class SecurityAuditorAware implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Long userId = SecurityUtils.getCurrentUserId();
        return Optional.ofNullable(userId != null ? userId : 1L);
    }
}
