package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationException;
import com.gym.crm.app.security.model.AuthenticatedUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserHolderTest {
    private CurrentUserHolder holder;
    private AuthenticatedUser testUser;

    @BeforeEach
    void setUp() {
        holder = new CurrentUserHolder();
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
        assertThrows(AuthentificationException.class, holder::get);
    }

    @Test
    void shouldClearUser() {
        holder.set(testUser);
        holder.clear();

        assertThrows(AuthentificationException.class, holder::get);
    }
}