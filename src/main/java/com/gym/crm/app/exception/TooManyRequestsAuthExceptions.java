package com.gym.crm.app.exception;

import lombok.Getter;

@Getter
public class TooManyRequestsAuthExceptions extends RuntimeException {
    private final long retryAfterSeconds;
    
    public TooManyRequestsAuthExceptions(String message, Long retryAfter) {
        super(message);
        this.retryAfterSeconds = retryAfter;
    }
}
