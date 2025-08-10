package com.gym.crm.app.service;

import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;


public interface TokenService {
    JwtTokenResponse login(LoginRequest loginRequest);
    JwtTokenResponse refresh(String refreshToken);
    void  logout(String token);
}
