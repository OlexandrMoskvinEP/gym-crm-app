package com.gym.crm.app.security;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserHolderTest {
    private static final ThreadLocal<AuthenticatedUser> CURRENT_USER = new ThreadLocal<>();

    private CurrentUserHolder holder;
    private AuthenticatedUser testUser;

    @BeforeEach
    void setUp() {
        holder = new CurrentUserHolder();
        testUser = AuthenticatedUser.builder().userId(1L).username("some.user").password("123456").isActive(true).build();
    }

    @Test
    void shouldSetCurrentUser() {
        holder.set(testUser);
        AuthenticatedUser result = holder.get();

        assertNotNull(result);
        assertEquals("some.user", result.getUsername());
    }

    @Test
    void shouldSuccessfullyReturnCurrentUser() {
        holder.set(testUser);
        AuthenticatedUser result = holder.get();

        assertNotNull(result);
        assertEquals("some.user", result.getUsername());

        assertDoesNotThrow(() -> CURRENT_USER.set(testUser));
    }

    @Test
    void shouldClearUser() {
        holder.set(testUser);
        holder.clear();

        assertThrows(AuthentificationErrorException.class, holder::get);
    }
}