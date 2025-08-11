package com.gym.crm.app.security.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gym.crm.app.security.service.LoginAttemptsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class LoginAttemptsServiceImpl implements LoginAttemptsService {
    private static final int MAX_ATTEMPTS = 3;
    private static final Duration BLOCK_DURATION = Duration.ofMinutes(5);

    private final Cache<String, Attempt> attempts = Caffeine.newBuilder()
            .expireAfterWrite(BLOCK_DURATION)
            .build();

    @Override
    public void loginSucceeded(String username) {
        attempts.invalidate(username);
    }

    @Override
    public void loginFailed(String username) {
        Attempt attempt = attempts.get(username, k -> new Attempt());
        int count = attempt.failures.incrementAndGet();

        if (count >= MAX_ATTEMPTS && attempt.blockedUntil == null) {
            attempt.blockedUntil = Instant.now().plus(BLOCK_DURATION);
        }
    }

    @Override
    public boolean isBlocked(String username) {
        Attempt attempt = attempts.getIfPresent(username);

        if (attempt == null || attempt.blockedUntil == null) return false;
        if (Instant.now().isBefore(attempt.blockedUntil)) return true;

        attempts.invalidate(username);

        return false;
    }

    @Override
    public long getRetryAfterSeconds(String username) {
        Attempt attempt = attempts.getIfPresent(username);
        if (attempt == null || attempt.blockedUntil == null) return 0;
        long sec = Duration.between(Instant.now(), attempt.blockedUntil).getSeconds();
        return Math.max(0, sec);
    }

    public static final class Attempt {
        private final AtomicInteger failures = new AtomicInteger(0);
        private volatile Instant blockedUntil;
    }
}
