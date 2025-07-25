package com.gym.crm.app.exception.handling;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.exception.RegistrationConflictException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.rest.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @ExceptionHandler(DataBaseErrorException.class)
    public ResponseEntity<ErrorResponse> handleDataBaseException(DataBaseErrorException exception) {
        log.error("DataBase exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.DATABASE_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .map(error -> String.format("%s: %s", error.getField(), error.getDefaultMessage()))
                .collect(Collectors.joining("; "));

        log.error("Validation failed: {}", message, exception);

        ErrorCode errorCode = ErrorCode.VALIDATION_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), message);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
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
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), message);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(UnacceptableOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnacceptable(UnacceptableOperationException exception) {
        log.error("Unacceptable exception occurred - {} ", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.UNACCEPTABLE_OPERATION;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(RegistrationConflictException.class)
    public ResponseEntity<ErrorResponse> handleRegistrationConflict(RegistrationConflictException exception) {
        log.error("Registration conflict exception occurred - {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.REGISTRATION_CONFLICT;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleUnhandledExceptions(Exception exception) {
        log.error("Unhandled Exception: {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.SERVER_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidJson(HttpMessageNotReadableException exception) {
        log.warn("Invalid JSON: {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException exception) {
        String message = exception.getMessage();

        if (exception.getRequiredType() != null) {
            message = String.format("Invalid value for parameter '%s': expected %s",
                    exception.getName(), exception.getRequiredType().getSimpleName());
        }

        log.error("Type mismatch: {}", message, exception);
        ErrorCode errorCode = ErrorCode.INVALID_REQUEST_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), message);

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthentificationErrorException.class)
    public ResponseEntity<ErrorResponse> handleAuthentificationException(AuthentificationErrorException exception) {
        log.error("Authentification exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.AUTHENTICATION_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }

    @ExceptionHandler(AuthorizationErrorException.class)
    public ResponseEntity<ErrorResponse> handleAuthorisationException(AuthorizationErrorException exception) {
        log.error("Authorisation exception occurred : {}", exception.getMessage(), exception);

        ErrorCode errorCode = ErrorCode.AUTHORIZATION_ERROR;
        ErrorResponse response = new ErrorResponse(errorCode.getErrorCode(), errorCode.getErrorMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(response);
    }
}
