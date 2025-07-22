package com.gym.crm.app.exception;

import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger("com.gym.crm.app");

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<ErrorResponse> handleDataBaseException(DataBaseException exception) {
        log.error("DataBase exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        log.error("Validation failed: {}", message, exception);

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException exception) {
        String message = exception.getConstraintViolations().stream()
                .map(violation -> String.format("%s: %s",
                        violation.getPropertyPath(),
                        violation.getMessage()))
                .collect(Collectors.joining("; "));

        log.error("Constraint violation: {}", message, exception);

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(UnacceptableOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnacceptable(UnacceptableOperationException exception) {
        log.error("Unacceptable exception occurred - {} ", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.UNACCEPTABLE_OPERATION;

        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(RegistrationConflictException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationConflict(RegistrationConflictException exception) {
        log.error("Registration conflict exception occurred - {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.REGISTRATION_CONFLICT;

        return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledExceptions(Exception exception) {
        log.error("Unhandled Exception: {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.SERVER_ERROR;

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException exception) {
        log.warn("Invalid JSON: {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_ERROR;

        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = String.format("Invalid value for parameter '%s': expected %s",
                exception.getName(), exception.getRequiredType().getSimpleName());

        log.warn("Type mismatch: {}", message, exception);

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_ERROR;

        return ResponseEntity.status(errorCode.getHttpStatus()).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(AuthentificationException.class)
    public ResponseEntity<ErrorResponse> handleAuthentificationException(AuthentificationException exception) {
        log.error("Authentification exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errorCode));
    }

    @ExceptionHandler(AuthorisationException.class)
    public ResponseEntity<ErrorResponse> handleAuthorisationException(AuthorisationException exception) {
        log.error("Authorisation exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.AUTHORIZATION_ERROR;

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(errorCode));
    }
}
