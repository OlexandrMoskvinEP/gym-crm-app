package com.gym.crm.app.exception.handling;

import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.exception.RegistrationConflictException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.rest.ErrorResponse;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Set;

import static com.gym.crm.app.exception.handling.ErrorCode.AUTHENTICATION_ERROR;
import static com.gym.crm.app.exception.handling.ErrorCode.AUTHORIZATION_ERROR;
import static com.gym.crm.app.exception.handling.ErrorCode.DATABASE_ERROR;
import static com.gym.crm.app.exception.handling.ErrorCode.INVALID_REQUEST_ERROR;
import static com.gym.crm.app.exception.handling.ErrorCode.REGISTRATION_CONFLICT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {
    private static final ErrorResponse VALIDATION_ERROR_RESPONSE = buildValidationResponse();
    private static final ErrorResponse UNACCEPTABLE_OPERATION_RESPONSE = buildUnacceptableErrorResponse();
    private static final ErrorResponse UNHANDLED_ERROR_RESPONSE = buildUnhandledErrorsResponse();
    private static final ErrorResponse INVALID_JSON_RESPONSE = buildInvalidJsonResponse();

    @Mock
    private Counter meterCounter;
    @Mock
    private MeterRegistry meterRegistry;
    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        lenient().when(meterRegistry.counter(anyString(), any(String[].class)))
                .thenReturn(meterCounter);
    }

    @Test
    void shouldReturnDatabaseError() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleDataBaseException(new DataBaseErrorException("checkedException"));

        assertEquals(DATABASE_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(DATABASE_ERROR.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(DATABASE_ERROR.getErrorCode(), entity.getBody().getErrorCode());

        verify(meterCounter).increment();
    }

    @Test
    void shouldReturnValidationError() {
        BindingResult bindingResult = buildBindingResult();

        MethodParameter methodParameter = mock(MethodParameter.class);
        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(methodParameter, bindingResult);

        when(methodParameter.getExecutable()).thenReturn(WrongRequest.class.getDeclaredConstructors()[0]);

        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleValidationException(exception);

        assertEquals(ErrorCode.VALIDATION_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(VALIDATION_ERROR_RESPONSE.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(VALIDATION_ERROR_RESPONSE.getErrorCode(), entity.getBody().getErrorCode());
    }

    @Test
    void shouldHandleConstraintViolationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        Path path = mock(Path.class);
        ConstraintViolationException exception = new ConstraintViolationException(Set.of(violation));

        when(path.toString()).thenReturn("field");
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn("must not be null");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleConstraintViolation(exception);

        assertEquals(ErrorCode.VALIDATION_ERROR.getHttpStatus(), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ErrorCode.VALIDATION_ERROR.getErrorCode(), response.getBody().getErrorCode());
        assertTrue(response.getBody().getErrorMessage().contains("field: must not be null"));
    }

    @Test
    void shouldHandleUnacceptableOperationException() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleUnacceptable(new UnacceptableOperationException("checkedException"));

        assertEquals(ErrorCode.UNACCEPTABLE_OPERATION.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(UNACCEPTABLE_OPERATION_RESPONSE.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(UNACCEPTABLE_OPERATION_RESPONSE.getErrorCode(), entity.getBody().getErrorCode());
    }

    @Test
    void shouldHandleRegistrationConflictException() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleRegistrationConflict(new RegistrationConflictException("checkedException"));

        assertEquals(REGISTRATION_CONFLICT.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(REGISTRATION_CONFLICT.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(REGISTRATION_CONFLICT.getErrorCode(), entity.getBody().getErrorCode());
    }

    @Test
    void handleUnhandledExceptions() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleUnhandledExceptions(new RuntimeException("checkedException"));

        assertEquals(ErrorCode.SERVER_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(UNHANDLED_ERROR_RESPONSE.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(UNHANDLED_ERROR_RESPONSE.getErrorCode(), entity.getBody().getErrorCode());
    }

    @Test
    void shouldHandleInvalidJson() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleInvalidJson(new HttpMessageNotReadableException("checkedException"));

        assertEquals(INVALID_REQUEST_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(INVALID_JSON_RESPONSE.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(INVALID_JSON_RESPONSE.getErrorCode(), entity.getBody().getErrorCode());
    }

    @Test
    void shouldHandleTypeMismatchIhRequestsParams() {
        MethodArgumentTypeMismatchException exception = new MethodArgumentTypeMismatchException(
                "abc", Integer.class, "id", null, new TypeMismatchException("abc", Integer.class));

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleTypeMismatch(exception);

        assertEquals(INVALID_REQUEST_ERROR.getHttpStatus(), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_REQUEST_ERROR.getErrorCode(), response.getBody().getErrorCode());
        assertTrue(response.getBody().getErrorMessage().contains("Invalid value for parameter 'id'"));
        assertTrue(response.getBody().getErrorMessage().contains("Integer"));
    }

    @Test
    void shouldHandleAuthentificationException() {
        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleAuthentificationException(new AuthentificationErrorException("checkedException"));

        assertEquals(AUTHENTICATION_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(AUTHENTICATION_ERROR.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(AUTHENTICATION_ERROR.getErrorCode(), entity.getBody().getErrorCode());

        verify(meterCounter).increment();
    }

    @Test
    void shouldHandleAuthorisationException() {
        AuthorizationErrorException authorizationException = new AuthorizationErrorException("checkedException");

        ResponseEntity<ErrorResponse> entity = exceptionHandler.handleAuthorisationException(authorizationException);

        assertEquals(AUTHORIZATION_ERROR.getHttpStatus(), entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertSame(AUTHORIZATION_ERROR.getErrorMessage(), entity.getBody().getErrorMessage());
        assertEquals(AUTHORIZATION_ERROR.getErrorCode(), entity.getBody().getErrorCode());

        verify(meterCounter).increment();
    }

    @Test
    void shouldHandleServiceCoreException() {
        CoreServiceException coreServiceException = new CoreServiceException("core service exception");

        ResponseEntity<ErrorResponse> response = exceptionHandler.handleCoreServiceException(coreServiceException);

        assertEquals(INVALID_REQUEST_ERROR.getHttpStatus(), response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(INVALID_REQUEST_ERROR.getErrorCode(), response.getBody().getErrorCode());
        assertEquals("Invalid request data: core service exception", response.getBody().getErrorMessage());
    }

    private static ErrorResponse buildUnacceptableErrorResponse() {
        return new ErrorResponse().errorCode(2910)
                .errorMessage("Unacceptable operation attempted");
    }

    private static ErrorResponse buildInvalidJsonResponse() {
        return new ErrorResponse().errorCode(2400)
                .errorMessage("Invalid request data: ");
    }

    private static ErrorResponse buildUnhandledErrorsResponse() {
        return new ErrorResponse().errorCode(3200)
                .errorMessage("Internal server error");
    }

    private static ErrorResponse buildValidationResponse() {
        return new ErrorResponse().errorCode(2760)
                .errorMessage("name: must not be blank");
    }

    private BindingResult buildBindingResult() {
        WrongRequest wrongRequest = new WrongRequest("");
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<WrongRequest>> violations = validator.validate(wrongRequest);

        BindingResult bindingResult = new BeanPropertyBindingResult(wrongRequest, "WrongRequest");
        violations.forEach(violation -> addErrorToBinding(violation, bindingResult));

        return bindingResult;
    }

    private void addErrorToBinding(ConstraintViolation<WrongRequest> violation, BindingResult bindingResult) {
        String field = violation.getPropertyPath().toString();
        String message = violation.getMessage();

        bindingResult.addError(new FieldError("WrongRequest", field, message));
    }

    @Getter
    static
    private class WrongRequest {
        @NotBlank
        private String name;

        public WrongRequest(String name) {
            this.name = name;
        }
    }
}