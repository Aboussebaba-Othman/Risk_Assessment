package com.riskassessment.auth.service;

import com.riskassessment.auth.dto.request.LoginRequest;
import com.riskassessment.auth.dto.request.RegisterRequest;
import com.riskassessment.auth.dto.response.AuthResponse;
import com.riskassessment.auth.dto.response.UserDto;
import com.riskassessment.auth.entity.Tenant;
import com.riskassessment.auth.entity.User;
import com.riskassessment.auth.entity.enums.SubscriptionPlan;
import com.riskassessment.auth.entity.enums.UserRole;
import com.riskassessment.auth.exception.DuplicateResourceException;
import com.riskassessment.auth.exception.UnauthorizedException;
import com.riskassessment.auth.repository.TenantRepository;
import com.riskassessment.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registration attempt for email: {}", request.getEmail());

        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already registered: " + request.getEmail());
        }

        // Créer ou récupérer le tenant
        Tenant tenant = tenantRepository.findByName(request.getTenantName())
                .orElseGet(() -> createTenant(request.getTenantName()));

        // Créer l'utilisateur
        User user = new User();
        user.setTenant(tenant);
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(UserRole.ADMIN); // Premier utilisateur = ADMIN
        user.setIsActive(true);

        user = userRepository.save(user);
        log.info("User registered successfully: {}", user.getEmail());

        // Générer les tokens
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getTenant().getId(),
                user.getRole().name()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Convertir en DTO
        UserDto userDto = userService.convertToDto(user);

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt for email: {}", request.getEmail());

        // Récupérer l'utilisateur
        User user = userService.findByEmail(request.getEmail());

        // Vérifier le mot de passe
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Vérifier si l'utilisateur est actif
        if (!user.getIsActive()) {
            throw new UnauthorizedException("Account is inactive");
        }

        // Vérifier si le tenant est actif
        if (!user.getTenant().getIsActive()) {
            throw new UnauthorizedException("Tenant account is inactive");
        }

        // Mettre à jour la dernière connexion
        userService.updateLastLogin(user.getEmail());

        // Générer les tokens
        String token = jwtService.generateToken(
                user.getEmail(),
                user.getTenant().getId(),
                user.getRole().name()
        );
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        // Convertir en DTO
        UserDto userDto = userService.convertToDto(user);

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .refreshToken(refreshToken)
                .user(userDto)
                .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        String email = jwtService.extractUsername(refreshToken);
        User user = userService.findByEmail(email);

        String newToken = jwtService.generateToken(
                user.getEmail(),
                user.getTenant().getId(),
                user.getRole().name()
        );
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        UserDto userDto = userService.convertToDto(user);

        return AuthResponse.builder()
                .token(newToken)
                .refreshToken(newRefreshToken)
                .user(userDto)
                .build();
    }

    private Tenant createTenant(String tenantName) {
        Tenant tenant = new Tenant();
        tenant.setName(tenantName);
        tenant.setSubscriptionPlan(SubscriptionPlan.STARTER);
        tenant.setSubscriptionStartDate(LocalDateTime.now());
        tenant.setSubscriptionEndDate(LocalDateTime.now().plusMonths(1)); // 1 mois gratuit
        tenant.setIsActive(true);
        tenant.setMaxUsers(5);
        tenant.setMaxCompanies(100);
        tenant.setMaxAnalysesPerMonth(50);

        return tenantRepository.save(tenant);
    }
}
