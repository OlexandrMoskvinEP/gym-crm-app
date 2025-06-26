package com.gym.crm.app.exception;

public class AlreadyExistUsernameException extends RuntimeException {
    public AlreadyExistUsernameException(String message) {
        super(message);
    }
}
