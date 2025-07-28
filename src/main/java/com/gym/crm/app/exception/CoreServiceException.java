package com.gym.crm.app.exception;

public class CoreServiceException extends RuntimeException {
    public CoreServiceException(String message) {
        super(message);
    }

    public CoreServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
