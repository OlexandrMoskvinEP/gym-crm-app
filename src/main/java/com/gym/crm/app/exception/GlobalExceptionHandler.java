package com.gym.crm.app.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("com.gym.crm.app");

    @ExceptionHandler(EntityNotFoundException.class)
    public String handleNotFound(EntityNotFoundException exception) {
        log.error("NotFound exception occurred", exception);

        return exception.getMessage();
    }

    @ExceptionHandler(DuplicateUsernameException.class)
    public String handleAlreadyExist(DuplicateUsernameException exception) {
        log.error("AlreadyExist exception occurred", exception);

        return exception.getMessage();
    }

    @ExceptionHandler(UnacceptableOperationException.class)
    public String handleUnacceptable(UnacceptableOperationException exception) {
        log.error("Unacceptable exception occurred", exception);

        return exception.getMessage();
    }
}
