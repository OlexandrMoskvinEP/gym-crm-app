package com.gym.crm.app.exception;

import lombok.Data;

import java.time.Instant;

@Data
public class ErrorResponse {
    private int status;
    private String message;
    private Instant timestamp;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.timestamp = Instant.now();
    }
}
