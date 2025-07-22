package com.gym.crm.app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("com.gym.crm.app");

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<ErrorResponse> handleDataBaseException(DataBaseException exception) {
        log.error("DataBase exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(UnacceptableOperationException.class)
    public String handleUnacceptable(UnacceptableOperationException exception) {
        log.error("Unacceptable exception occurred", exception);

        return exception.getMessage();
    }
}
