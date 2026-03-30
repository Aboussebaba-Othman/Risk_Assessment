package com.riskassessment.auth.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakTenantSetupRunner implements ApplicationRunner {

    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:risk-assessment}")
    private String realm;

    @Value("${keycloak.resource:risk-assessment-backend}")
    private String clientId;

    @Value("${keycloak.admin.username:${KEYCLOAK_ADMIN_USERNAME:admin}}")
    private String adminUsername;

    @Value("${keycloak.admin.password:${KEYCLOAK_ADMIN_PASSWORD:admin}}")
    private String adminPassword;

    private final RestTemplate restTemplate;
    private final AuthServiceImpl authService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            String adminToken = authService.getAdminTokenPublic();
            enableUnmanagedAttributes(adminToken);
            ensureAdminHasTenantId(adminToken);
            ensureProtocolMapperExists(adminToken);
        } catch (Exception e) {
            log.warn("KeycloakTenantSetup: Could not complete setup on startup (Keycloak may not be ready): {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void enableUnmanagedAttributes(String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String profileUrl = keycloakUrl + "/admin/realms/" + realm + "/users/profile";
        try {
            ResponseEntity<Map<?, ?>> resp = restTemplate.exchange(profileUrl, HttpMethod.GET, new HttpEntity<>(headers), (Class<Map<?, ?>>)(Class<?>)Map.class);
            if (resp.getBody() == null) return;

            Map<String, Object> profile = (Map<String, Object>) resp.getBody();
            Object currentPolicy = profile.get("unmanagedAttributePolicy");

            if ("ADMIN_VIEW".equals(currentPolicy) || "ENABLED".equals(currentPolicy)) {
                log.debug("KeycloakTenantSetup: unmanagedAttributePolicy already configured: {}", currentPolicy);
                return;
            }

            profile.put("unmanagedAttributePolicy", "ADMIN_VIEW");
            restTemplate.exchange(profileUrl, HttpMethod.PUT, new HttpEntity<>(profile, headers), Void.class);
            log.info("KeycloakTenantSetup: Set unmanagedAttributePolicy=ADMIN_VIEW to allow custom attributes like 'tenant_id'");
        } catch (Exception e) {
            log.warn("KeycloakTenantSetup: Could not configure user profile to allow custom attributes: {}", e.getMessage());
        }
    }

    private void ensureAdminHasTenantId(String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        // Get admin user
        String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users?username=admin&exact=true";
        ResponseEntity<List<?>> resp = restTemplate.exchange(usersUrl, HttpMethod.GET, new HttpEntity<>(headers), (Class<List<?>>)(Class<?>)List.class);

        if (resp.getBody() == null || resp.getBody().isEmpty()) {
            log.warn("KeycloakTenantSetup: admin user not found in realm {}", realm);
            return;
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> adminUser = (Map<String, Object>) resp.getBody().get(0);
        String adminId = (String) adminUser.get("id");

        // Check if tenant_id already set
        @SuppressWarnings("unchecked")
        Map<String, Object> attrs = (Map<String, Object>) adminUser.getOrDefault("attributes", new java.util.HashMap<>());
        if (attrs != null && attrs.containsKey("tenant_id")) {
            log.info("KeycloakTenantSetup: admin already has tenant_id={}", attrs.get("tenant_id"));
            return;
        }

        if (attrs == null) {
            attrs = new java.util.HashMap<>();
        }
        
        // Set tenant_id=1 on admin
        attrs.put("tenant_id", List.of("1"));
        adminUser.put("attributes", attrs);

        String updateUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + adminId;
        try {
            restTemplate.exchange(updateUrl, HttpMethod.PUT, new HttpEntity<>(adminUser, headers), Void.class);
            log.info("KeycloakTenantSetup: Set tenant_id=1 on admin user");
        } catch (Exception e) {
            log.warn("KeycloakTenantSetup: Could not set admin tenant_id: {}", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void ensureProtocolMapperExists(String adminToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        // Get client ID (internal UUID)
        String clientsUrl = keycloakUrl + "/admin/realms/" + realm + "/clients?clientId=" + clientId;
        ResponseEntity<List<?>> clientsResp = restTemplate.exchange(clientsUrl, HttpMethod.GET, new HttpEntity<>(headers), (Class<List<?>>)(Class<?>)List.class);
        if (clientsResp.getBody() == null || clientsResp.getBody().isEmpty()) {
            log.warn("KeycloakTenantSetup: Client '{}' not found", clientId);
            return;
        }

        Map<String, Object> client = (Map<String, Object>) clientsResp.getBody().get(0);
        String clientUuid = (String) client.get("id");

        // Check existing mappers
        String mappersUrl = keycloakUrl + "/admin/realms/" + realm + "/clients/" + clientUuid + "/protocol-mappers/models";
        ResponseEntity<List<?>> mappersResp = restTemplate.exchange(mappersUrl, HttpMethod.GET, new HttpEntity<>(headers), (Class<List<?>>)(Class<?>)List.class);

        boolean mapperExists = false;
        if (mappersResp.getBody() != null) {
            mapperExists = mappersResp.getBody().stream()
                    .anyMatch(m -> "tenant_id".equals(((Map<String, Object>) m).get("name")));
        }

        if (mapperExists) {
            log.info("KeycloakTenantSetup: tenant_id protocol mapper already exists");
            return;
        }

        // Create user-attribute mapper for tenant_id
        Map<String, Object> mapper = Map.of(
                "name", "tenant_id",
                "protocol", "openid-connect",
                "protocolMapper", "oidc-usermodel-attribute-mapper",
                "config", Map.of(
                        "user.attribute", "tenant_id",
                        "claim.name", "tenant_id",
                        "jsonType.label", "long",
                        "id.token.claim", "true",
                        "access.token.claim", "true",
                        "userinfo.token.claim", "true",
                        "multivalued", "false",
                        "aggregate.attrs", "false"
                ));

        try {
            headers.setContentType(MediaType.APPLICATION_JSON);
            restTemplate.exchange(mappersUrl, HttpMethod.POST, new HttpEntity<>(mapper, headers), Void.class);
            log.info("KeycloakTenantSetup: Created tenant_id protocol mapper for client '{}'", clientId);
        } catch (HttpClientErrorException.Conflict e) {
            log.info("KeycloakTenantSetup: tenant_id mapper already exists (conflict)");
        } catch (Exception e) {
            log.warn("KeycloakTenantSetup: Could not create protocol mapper: {}", e.getMessage());
        }
    }
}
