package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CurrentUserHolderTest {
    private CurrentUserHolder holder;
    private User testUser;

    @BeforeEach
    void setUp() {
        holder = new CurrentUserHolder();
        testUser = new User().toBuilder().id(1L).username("john.doe").build();
    }

    @Test
    void shouldSetAndGetUser() {
        holder.set(testUser);
        User result = holder.get();

        assertNotNull(result);
        assertEquals("john.doe", result.getUsername());
    }

    @Test
    void shouldThrowIfUserNotSet() {
        assertThrows(SecurityException.class, holder::get);
    }

    @Test
    void shouldClearUser() {
        holder.set(testUser);
        holder.clear();

        assertThrows(SecurityException.class, holder::get);
    }
}