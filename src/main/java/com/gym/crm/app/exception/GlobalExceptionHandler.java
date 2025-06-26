package com.gym.crm.app.exception;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("com.gym.crm.app");

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(NotFoundException exception) {
        log.error("NotFound exception occurred", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(AlreadyExistException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExist(AlreadyExistException exception) {
        log.error("AlreadyExist exception occurred", exception);
        return exception.getMessage();
    }

    @ExceptionHandler(UnacceptableOperationException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleUnacceptable(UnacceptableOperationException exception) {
        log.error("Unacceptable exception occurred", exception);
        return exception.getMessage();
    }
}
