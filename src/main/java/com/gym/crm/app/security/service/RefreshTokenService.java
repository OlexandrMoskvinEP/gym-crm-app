package com.gym.crm.app.security.service;

import com.gym.crm.app.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    public String saveRefreshToken(RefreshToken refreshToken, Long userId);

    public Optional<Long> validateAndGetUserId(String refreshToken);

    public void delete(String refreshToken);

    public void deleteAllByUserId(Long userId);
}
