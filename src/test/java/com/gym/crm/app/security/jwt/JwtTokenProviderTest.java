package com.gym.crm.app.security.jwt;

import com.gym.crm.app.security.model.AuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static com.gym.crm.app.security.UserRole.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    private final static AuthenticatedUser AUTHENTICATED_USER = buildAuthUser();
    private final static String TEST_TOKEN = getToken();

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    void shouldGenerateCorrectToken() {
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
    void shouldParseToken() {
        AuthenticatedUser decodedUser = jwtTokenProvider.parseToken(TEST_TOKEN);

        assertNotNull(decodedUser);
        assertEquals(AUTHENTICATED_USER.getUsername(), decodedUser.getUsername());
        assertEquals(AUTHENTICATED_USER.getPassword(), decodedUser.getPassword());
        assertEquals(AUTHENTICATED_USER.getRole(), decodedUser.getRole());
        assertEquals(AUTHENTICATED_USER.getIsActive(), decodedUser.getIsActive());

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

    private static String getToken() {
        return "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJwZXJmZWN0LnVzZXIiLCJ1c2VyIjoie" +
                "1widXNlcklkXCI6MjMsXCJ1c2VybmFtZVwiOlwicGVyZmVjdC51c2VyXCIsXCJwY" +
                "XNzd29yZFwiOlwiOTAyODM3cjg5YzczdHI4dmI5MDh3NGN0dnlcIixcInJvbGVcI" +
                "jpcIkFETUlOXCIsXCJpc0FjdGl2ZVwiOnRydWV9IiwiaWF0IjoxNzU0NjQ4Nzg4LCJ" +
                "leHAiOjE3NTQ2NTIzODh9.PpRi8dYXDw29M3MU1dYuY6agUpKeORsCI0rQEiaLMAlQkw8pZA-6d2K2NKLcBJT7";
    }
}