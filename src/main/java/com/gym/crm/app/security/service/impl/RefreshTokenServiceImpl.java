package com.gym.crm.app.security.service.impl;

import com.gym.crm.app.domain.model.RefreshToken;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.repository.RefreshTokenRepository;
import com.gym.crm.app.security.jwt.JwtTokenProvider;
import com.gym.crm.app.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public String saveRefreshToken(RefreshToken refreshToken, Long userId) {
        String rawToken = refreshToken.getToken();
        String hashedToken = hashToken(rawToken);

        RefreshToken tokenToSave = refreshToken.toBuilder()
                .token(hashedToken)
                .userId(userId).build();

        refreshTokenRepository.save(tokenToSave);

        return rawToken;
    }

    @Override
    public Optional<Long> validateAndGetUserId(String refreshToken) {
        String hashedToken = hashToken(refreshToken);

        return refreshTokenRepository.findById(hashedToken)
                .filter(rt -> rt.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshToken::getUserId);
    }

    @Override
    public void delete(String refreshToken) {
        refreshTokenRepository.deleteById(hashToken(refreshToken));
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);

    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new CoreServiceException(e.getMessage());
        }
    }
}
