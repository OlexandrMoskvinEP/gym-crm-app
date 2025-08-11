package com.gym.crm.app.security.service;

import com.gym.crm.app.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    String saveRefreshToken(RefreshToken refreshToken, Long userId);

    Optional<Long> validateAndGetUserId(String refreshToken);

    void delete(String refreshToken);

    void deleteAllByUserId(Long userId);
}
