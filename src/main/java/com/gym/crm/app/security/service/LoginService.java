package com.gym.crm.app.security.service;

import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;

public interface LoginService {
    JwtTokenResponse login(LoginRequest loginRequest);

    JwtTokenResponse refresh(String refreshToken);

    void logout(String token);
}
