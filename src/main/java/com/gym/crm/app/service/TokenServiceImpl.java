package com.gym.crm.app.service;

import com.gym.crm.app.domain.model.RefreshToken;
import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.security.CurrentUserHolder;
import com.gym.crm.app.security.jwt.JwtTokenProvider;
import com.gym.crm.app.security.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {
    private static final Duration REFRESH_TTL = Duration.ofDays(14);

    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationService authenticationService;
    private final CurrentUserHolder currentUserHolder;

    @Override
    public JwtTokenResponse login(LoginRequest loginRequest) {
        AuthenticatedUser user = authenticationService.getAuthenticatedUser(loginRequest);

        String accessToken = jwtTokenProvider.generateToken(user);

        RefreshToken refresh = jwtTokenProvider.generateRefreshToken(user.getUserId(), REFRESH_TTL);
        String rawRefresh = refreshTokenService.saveRefreshToken(refresh, user.getUserId());

        currentUserHolder.set(user);

        JwtTokenResponse response = new JwtTokenResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(rawRefresh);

        return response;
    }

    @Override
    public JwtTokenResponse refresh(String refreshToken) {
        return null;
    }
}
