package com.gym.crm.app.security.service;

public interface LoginAttemptsService {
    void loginSucceeded(String username);

    void loginFailed(String username);

    boolean isBlocked(String username);

    long getRetryAfterSeconds(String username);
}
