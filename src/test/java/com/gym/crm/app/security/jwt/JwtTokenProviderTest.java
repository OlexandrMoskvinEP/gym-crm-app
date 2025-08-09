package com.gym.crm.app.security.jwt;

import com.gym.crm.app.domain.model.RefreshToken;
import com.gym.crm.app.security.model.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;

import static com.gym.crm.app.security.UserRole.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    private static final AuthenticatedUser AUTHENTICATED_USER = buildAuthUser();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldHandleTokenCorrectly() {
        String token = jwtTokenProvider.generateToken(AUTHENTICATED_USER);

        assertNotNull(token);

        AuthenticatedUser decodedUser = jwtTokenProvider.parseToken(token);

        assertNotNull(decodedUser);
        assertEquals(AUTHENTICATED_USER.getUsername(), decodedUser.getUsername());
        assertEquals(AUTHENTICATED_USER.getPassword(), decodedUser.getPassword());
        assertEquals(AUTHENTICATED_USER.getRole(), decodedUser.getRole());
        assertEquals(AUTHENTICATED_USER.getIsActive(), decodedUser.getIsActive());
    }

    @Test
    void shouldGenerateRefreshToken() {
        long userId = 42L;
        Duration ttl = Duration.ofHours(2);

        RefreshToken rt = jwtTokenProvider.generateRefreshToken(userId, ttl);

        assertNotNull(rt);
        assertEquals(userId, rt.getUserId());
        assertNotNull(rt.getToken());
        assertNotNull(rt.getIssuedAt());
        assertNotNull(rt.getExpiresAt());
    }

    private static AuthenticatedUser buildAuthUser() {
        return AuthenticatedUser.builder()
                .role(ADMIN)
                .userId(23L)
                .username("perfect.user")
                .password("902837r89c73tr8vb908w4ctvy")
                .isActive(true)
                .build();
    }
}