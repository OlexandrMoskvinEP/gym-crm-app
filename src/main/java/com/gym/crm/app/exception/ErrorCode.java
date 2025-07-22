package com.gym.crm.app.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_REQUEST_ERROR(2400, "Invalid or malformed request data", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(2760, "Validation error", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR(2805, "Authentication failed", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR(2806, "Access denied", HttpStatus.FORBIDDEN),
    NOT_FOUND_ERROR(2835, "Resource not found", HttpStatus.NOT_FOUND),
    DATABASE_ERROR(3358, "Unexpected database failure", HttpStatus.INTERNAL_SERVER_ERROR),
    SERVER_ERROR(3200, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    UNACCEPTABLE_OPERATION(2910, "Unacceptable operation attempted",HttpStatus.CONFLICT),
    REGISTRATION_CONFLICT(2911, "Username already exists", HttpStatus.CONFLICT);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
