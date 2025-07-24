package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserHolderTest {
    private CurrentUserHolder holder;
    private AuthenticatedUser testUser;
    private final HttpServletRequest request = new MockHttpServletRequest();

    @BeforeEach
    void setUp() {
        holder = new CurrentUserHolder(request);
        testUser = AuthenticatedUser.builder().userId(1L).username("john.doe").password("123456").isActive(true).build();
    }

    @Test
    void shouldSetAndGetUser() {
        holder.set(testUser);
        AuthenticatedUser result = holder.get();

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void shouldThrowIfUserNotSet() {
        assertThrows(AuthentificationErrorException.class, holder::get);
    }

    @Test
    void shouldClearUser() {
        holder.set(testUser);
        holder.clear();

        assertThrows(AuthentificationErrorException.class, holder::get);
    }
}