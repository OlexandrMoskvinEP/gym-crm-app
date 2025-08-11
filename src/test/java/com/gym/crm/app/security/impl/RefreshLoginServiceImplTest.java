package com.gym.crm.app.security.impl;

import com.gym.crm.app.domain.model.RefreshToken;
import com.gym.crm.app.repository.RefreshTokenRepository;
import com.gym.crm.app.security.service.impl.RefreshTokenServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshLoginServiceImplTest {
    private static final String RAW_TOKEN = "rawToken123";
    private static final Long USER_ID = 42L;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @InjectMocks
    private RefreshTokenServiceImpl refreshTokenService;

    @Test
    void saveRefreshToken_ShouldHashAndNotReuseOriginalInstance() {
        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        RefreshToken rawToken = getRawToken();

        String returned = refreshTokenService.saveRefreshToken(rawToken, USER_ID);

        verify(refreshTokenRepository).save(captor.capture());
        RefreshToken saved = captor.getValue();

        assertEquals(RAW_TOKEN, returned);
        assertNotSame(rawToken, saved);
        assertNotEquals(RAW_TOKEN, saved.getToken());
        assertTrue(saved.getToken().matches("^[0-9a-f]{64}$"));
        assertEquals(USER_ID, saved.getUserId());
        verify(refreshTokenRepository, never()).save(same(rawToken));
    }

    @Test
    void shouldValidateAndReturnUserId_IfValid() {
        RefreshToken storedToken = getHashedToken();
        when(refreshTokenRepository.findById(anyString())).thenReturn(Optional.of(storedToken));

        Optional<Long> result = refreshTokenService.validateAndGetUserId(RAW_TOKEN);

        assertTrue(result.isPresent());
        assertEquals(USER_ID, result.get());
    }

    @Test
    void shouldValidateAndReturnEmpty_IfExpired() {
        RefreshToken expiredToken = getExpiredRefreshToken();
        when(refreshTokenRepository.findById(anyString())).thenReturn(Optional.of(expiredToken));

        Optional<Long> result = refreshTokenService.validateAndGetUserId(RAW_TOKEN);

        assertTrue(result.isEmpty());
    }

    @Test
    void shouldDeleteTokenById() {
        refreshTokenService.delete(RAW_TOKEN);

        verify(refreshTokenRepository, times(1)).deleteById(anyString());
    }

    @Test
    void shouldDeleteTokenByUserId() {
        refreshTokenService.deleteAllByUserId(USER_ID);

        verify(refreshTokenRepository, times(1)).deleteByUserId(USER_ID);
    }

    private static RefreshToken getHashedToken() {
        return RefreshToken.builder()
                .token("hashedValue")
                .userId(USER_ID)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    private static RefreshToken getExpiredRefreshToken() {
        return RefreshToken.builder()
                .token("hashedValue")
                .userId(USER_ID)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().minusSeconds(10)) // expired
                .build();
    }

    private static RefreshToken getRawToken() {
        return RefreshToken.builder()
                .token(RAW_TOKEN)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }
}