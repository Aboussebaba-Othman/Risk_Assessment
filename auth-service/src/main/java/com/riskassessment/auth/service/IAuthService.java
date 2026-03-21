package com.riskassessment.auth.service;

import com.riskassessment.auth.dto.AuthResponse;
import com.riskassessment.auth.dto.LoginRequest;
import com.riskassessment.auth.dto.RegisterRequest;

import java.util.Map;

public interface IAuthService {
    Map<String, Object> register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
