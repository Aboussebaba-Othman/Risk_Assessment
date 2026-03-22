package com.riskassessment.analysis.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
public class JwtUtil {

    private final ObjectMapper mapper = new ObjectMapper();

    public String extractUserId(String token) {
        return extractClaim(token, "sub");
    }

    public String extractPreferredUsername(String token) {
        return extractClaim(token, "preferred_username");
    }

    private String extractClaim(String token, String claim) {
        if (token == null || !token.startsWith("Bearer ")) {
            return null;
        }
        try {
            String payload = token.split("\\.")[1];
            String decodedString = new String(Base64.getUrlDecoder().decode(payload));
            JsonNode payloadObj = mapper.readTree(decodedString);
            return payloadObj.has(claim) ? payloadObj.get(claim).asText() : null;
        } catch (Exception e) {
            log.warn("Failed to decode JWT to extract claim {}: {}", claim, e.getMessage());
            return null;
        }
    }
}
