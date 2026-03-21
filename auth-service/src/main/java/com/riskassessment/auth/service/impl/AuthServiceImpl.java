package com.riskassessment.auth.service.impl;

import com.riskassessment.auth.dto.AuthResponse;
import com.riskassessment.auth.dto.LoginRequest;
import com.riskassessment.auth.dto.RegisterRequest;
import com.riskassessment.auth.exception.ExternalServiceException;
import com.riskassessment.auth.exception.UnauthorizedException;
import com.riskassessment.auth.service.IAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    @Value("${keycloak.auth-server-url:http://keycloak:8080}")
    private String keycloakUrl;

    @Value("${keycloak.realm:risk-assessment}")
    private String realm;

    @Value("${keycloak.resource:risk-assessment-backend}")
    private String clientId;

    @Value("${keycloak.credentials.secret:i1Asr3asyUuYHeXhokzGmBuU6qoRm0sT}")
    private String clientSecret;

    @Value("${keycloak.admin.username:${KEYCLOAK_ADMIN_USERNAME:admin}}")
    private String adminUsername;

    @Value("${keycloak.admin.password:${KEYCLOAK_ADMIN_PASSWORD:admin}}")
    private String adminPassword;

    private final RestTemplate restTemplate;

    // ── Register: create user in Keycloak
    public Map<String, Object> register(RegisterRequest request) {
        // 1. Get admin token
        String adminToken = getAdminToken();

        // 2. Create user in Keycloak
        String createUserUrl = keycloakUrl + "/admin/realms/" + realm + "/users";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> userRepresentation = Map.of(
                "username", request.getUsername(),
                "email", request.getEmail(),
                "enabled", true,
                "emailVerified", true,
                "requiredActions", List.of(),
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", request.getPassword(),
                        "temporary", false)));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userRepresentation, headers);
        ResponseEntity<Void> response = restTemplate.exchange(createUserUrl, HttpMethod.POST, entity, Void.class);

        if (response.getStatusCode() == HttpStatus.CREATED) {
            log.info("User {} created in Keycloak successfully", request.getUsername());
            // Assign realm roles
            if (request.getRoles() != null && !request.getRoles().isEmpty()) {
                assignRoles(adminToken, request.getUsername(), request.getRoles());
            }
            return Map.of("message", "User registered successfully", "username", request.getUsername());
        } else {
            throw new ExternalServiceException("Failed to create user in Keycloak: " + response.getStatusCode());
        }
    }

    // ── Login: get token from Keycloak ─────────────────────────────────────
    public AuthResponse login(LoginRequest request) {
        String tokenUrl = keycloakUrl + "/realms/" + realm + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("username", request.getUsername());
        body.add("password", request.getPassword());
        body.add("scope", "openid profile email");

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);
            Map<String, Object> tokenMap = response.getBody();

            return AuthResponse.builder()
                    .accessToken((String) tokenMap.get("access_token"))
                    .refreshToken((String) tokenMap.get("refresh_token"))
                    .expiresIn(((Number) tokenMap.get("expires_in")).longValue())
                    .tokenType("Bearer")
                    .username(request.getUsername())
                    .build();
        } catch (Exception e) {
            log.error("Login failed for user {}: {}", request.getUsername(), e.getMessage());
            throw new UnauthorizedException("Invalid credentials or Keycloak unreachable: " + e.getMessage());
        }
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private String getAdminToken() {
        String tokenUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", "admin-cli");
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, Map.class);

        return (String) response.getBody().get("access_token");
    }

    @SuppressWarnings("unchecked")
    private void assignRoles(String adminToken, String username, List<String> roleNames) {
        try {
            // Get user ID
            String usersUrl = keycloakUrl + "/admin/realms/" + realm + "/users?username=" + username + "&exact=true";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(adminToken);

            ResponseEntity<List> usersResponse = restTemplate.exchange(usersUrl, HttpMethod.GET,
                    new HttpEntity<>(headers), List.class);
            List<Map<String, Object>> users = usersResponse.getBody();
            if (users == null || users.isEmpty())
                return;

            String userId = (String) users.get(0).get("id");

            // Get realm roles
            String rolesUrl = keycloakUrl + "/admin/realms/" + realm + "/roles";
            ResponseEntity<List> rolesResponse = restTemplate.exchange(rolesUrl, HttpMethod.GET,
                    new HttpEntity<>(headers), List.class);
            List<Map<String, Object>> allRoles = rolesResponse.getBody();

            // Filter roles to assign
            List<Map<String, Object>> rolesToAssign = allRoles.stream()
                    .filter(r -> roleNames.stream().anyMatch(rn -> rn.equalsIgnoreCase((String) r.get("name"))))
                    .toList();

            if (!rolesToAssign.isEmpty()) {
                String assignUrl = keycloakUrl + "/admin/realms/" + realm + "/users/" + userId + "/role-mappings/realm";
                HttpEntity<List<Map<String, Object>>> assignEntity = new HttpEntity<>(rolesToAssign, headers);
                restTemplate.exchange(assignUrl, HttpMethod.POST, assignEntity, Void.class);
                log.info("Assigned roles {} to user {}", roleNames, username);
            }
        } catch (Exception e) {
            log.warn("Could not assign roles: {}", e.getMessage());
        }
    }
}
