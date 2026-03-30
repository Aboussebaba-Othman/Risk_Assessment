package com.riskassessment.company.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;


import java.util.List;
import java.util.stream.Collectors;

public class SecurityUtils {

    private static final Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    private SecurityUtils() {
    }


    public static Long getTenantId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            var claims = jwtAuth.getToken().getClaims();
            log.info("JWT Claims: {}", claims.keySet());
            log.info("tenant_id claim value: {}, type: {}", 
                claims.get("tenant_id"), 
                claims.get("tenant_id") != null ? claims.get("tenant_id").getClass().getName() : "null");

            Object tenantIdClaim = jwtAuth.getToken().getClaim("tenant_id");
            if (tenantIdClaim != null) {
                try {
                    if (tenantIdClaim instanceof Number n) return n.longValue();
                    return Long.parseLong(tenantIdClaim.toString());
                } catch (NumberFormatException ignored) {
                    log.warn("tenant_id claim '{}' is not parseable as Long", tenantIdClaim);
                }
            }
            String sub = jwtAuth.getToken().getClaimAsString("sub");
            if (sub != null) {
                try {
                    return Long.valueOf(sub);
                } catch (NumberFormatException ignored) {
                }
            }
        } else {
            log.info("Auth is not JwtAuthenticationToken, type: {}", auth != null ? auth.getClass().getName() : "null");
        }
        return null;
    }

    public static Long getCurrentUserId() {
        return getTenantId();
    }

    public static String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken().getClaimAsString("preferred_username");
        }
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : null;
    }


    public static List<String> getCurrentUserRoles() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            return auth.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
        }
        return List.of();
    }
}
