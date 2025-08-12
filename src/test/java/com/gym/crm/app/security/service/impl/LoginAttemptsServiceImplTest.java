package com.gym.crm.app.security.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LoginAttemptsServiceImplTest {
    private LoginAttemptsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new LoginAttemptsServiceImpl();
    }

    @Test
    void shouldResetAttemptsOnSuccess() {
        service.loginFailed("user1");
        service.loginSucceeded("user1");

        assertFalse(service.isBlocked("user1"));
    }

    @Test
    void shouldBlockAfterThreeFailures() {
        service.loginFailed("user1");
        service.loginFailed("user1");
        service.loginFailed("user1");

        assertTrue(service.isBlocked("user1"));
    }

    @Test
    void shouldNotBeBlockedInitially() {
        assertFalse(service.isBlocked("user1"));
    }

    @Test
    void shouldReturnRetryAfterWhenBlocked() {
        service.loginFailed("user1");
        service.loginFailed("user1");
        service.loginFailed("user1");

        long retryAfter = service.getRetryAfterSeconds("user1");
        assertTrue(retryAfter > 0 && retryAfter <= 300);
    }
}