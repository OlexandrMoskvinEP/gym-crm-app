package com.gym.crm.app.exception;

public class RegistrationConflictException extends RuntimeException {
    public RegistrationConflictException(String message) {
        super(message);
    }
}
