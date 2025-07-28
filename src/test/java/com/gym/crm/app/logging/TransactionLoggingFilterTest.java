package com.gym.crm.app.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {
    private ListAppender<ILoggingEvent> logAppender;
    private TransactionLoggingFilter filter;

    @BeforeEach
    void setup() {
        filter = new TransactionLoggingFilter();

        Logger logger = (Logger) LoggerFactory.getLogger(TransactionLoggingFilter.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @Test
    void shouldLogRequestNormally() throws ServletException, IOException {
        MockHttpServletRequest request = buildRequest("POST", "/fakeURL", "test-body");
        ContentCachingRequestWrapper wrappedRequest = buildWrappedRequest(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = buildFilterChain("response-body");

        filter.doFilter(wrappedRequest, response, chain);

        ILoggingEvent loggingEvent = logAppender.list.iterator().next();
        assertThat(loggingEvent.getFormattedMessage()).contains("test-body");
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void shouldLogRequestEndpoint() throws ServletException, IOException {
        MockHttpServletRequest request = buildRequest("POST", "/api/v1/trainees/register", "test-body");
        ContentCachingRequestWrapper wrappedRequest = buildWrappedRequest(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = buildFilterChain("response-body");

        filter.doFilter(wrappedRequest, response, chain);

        ILoggingEvent loggingEvent = logAppender.list.iterator().next();
        assertThat(loggingEvent.getFormattedMessage()).contains("POST /api/v1/trainees/register");
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void shouldLogResponseNormally() throws ServletException, IOException {
        MockHttpServletRequest request = buildRequest("POST", "/fakeURL", "test-body");
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = buildFilterChain("response-body");

        filter.doFilter(wrappedRequest, response, chain);

        ILoggingEvent loggingEvent = logAppender.list.iterator().next();
        assertThat(loggingEvent.getFormattedMessage()).contains("test-body");
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void shouldLogErrorWhenExceptionOccurs() throws ServletException, IOException {
        FilterChain mockChain = mock(FilterChain.class);
        MockHttpServletRequest request = buildRequest("POST", "/error", "fail");
        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new RuntimeException("Simulated error")).when(mockChain)
                .doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

        assertThatThrownBy(() -> filter.doFilter(request, response, mockChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Simulated error");

        ILoggingEvent loggingEvent = logAppender.list.iterator().next();
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvent.getFormattedMessage()).contains("Exception during filter chain: Simulated error");
    }

    @Test
    void shouldMaskPasswordFieldsInChangePasswordRequestBody() throws Exception {
        String body = buildPassUpdateRequest();
        MockHttpServletRequest request = buildRequest("PUT", "/api/v1/change-password", body);
        FilterChain filterChain = buildFilterChain(body);

        filter.doFilterInternal(request, buildWrappedResponse(), filterChain);

        ILoggingEvent logEvent = logAppender.list.iterator().next();
        assertThat(logEvent.getFormattedMessage())
                .contains("\"username\": \"will.bezucha\"", "\"oldPassword\": \"*****\"", "\"newPassword\": \"*****\"")
                .doesNotContain("qwerty1234", "qwerty9999");
    }

    @Test
    void shouldMaskPasswordFieldsInLoginRequestBody() throws Exception {
        String body = buildAuthCredentials();
        MockHttpServletRequest request = buildRequest("POST", "/api/v1/login", body);
        FilterChain filterChain = buildFilterChain(body);

        filter.doFilterInternal(request, buildWrappedResponse(), filterChain);

        ILoggingEvent logEvent = logAppender.list.iterator().next();
        assertThat(logEvent.getFormattedMessage())
                .contains("\"username\": \"will.bezucha\"", "\"password\": \"*****\"")
                .doesNotContain("qwerty1234");
    }

    @Test
    void shouldMaskPasswordFieldsInRegisterResponseBody() throws Exception {
        String body = buildAuthCredentials();
        MockHttpServletRequest request = buildRequest("PUT", "/api/v1/trainees/register", body);
        FilterChain chain = buildFilterChain(body);

        filter.doFilterInternal(request, buildWrappedResponse(), chain);

        ILoggingEvent logEvent = logAppender.list.iterator().next();
        assertThat(logEvent.getFormattedMessage())
                .contains("\"username\": \"will.bezucha\"", "\"password\": \"*****\"")
                .doesNotContain("qwerty1234");
    }

    private FilterChain buildFilterChain(String body) {
        return (req, res) -> {
            req.getReader().lines().forEach(line -> {
            });
            ((HttpServletResponse) res).setStatus(200);
            res.getWriter().write(body);
        };
    }

    private MockHttpServletRequest buildRequest(String httpMethod, String endpoint, String body) {
        MockHttpServletRequest request = new MockHttpServletRequest(httpMethod, endpoint);
        request.setContent(body.getBytes());
        request.setCharacterEncoding("UTF-8");

        return request;
    }

    private ContentCachingRequestWrapper buildWrappedRequest(MockHttpServletRequest request) {
        return new ContentCachingRequestWrapper(request);
    }

    private ContentCachingResponseWrapper buildWrappedResponse() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        return new ContentCachingResponseWrapper(response);
    }

    private String buildPassUpdateRequest() {
        return """
                {
                    "username": "will.bezucha",
                    "oldPassword": "qwerty1234",
                    "newPassword": "qwerty9999"
                }
                """;
    }

    private String buildAuthCredentials() {
        return """
                {
                    "username": "will.bezucha",
                    "password": "qwerty1234"
                }
                """;
    }
}
