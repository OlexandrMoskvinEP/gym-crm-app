package com.gym.crm.app.logging;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
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
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {

    private TransactionLoggingFilter filter;
    private ListAppender<ILoggingEvent> logAppender;

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
        List<ILoggingEvent> logs = logAppender.list;
        MockHttpServletRequest baseRequest = new MockHttpServletRequest("POST", "/fakeURL");
        baseRequest.setContent("test-body".getBytes());
        baseRequest.setCharacterEncoding("UTF-8");

        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(baseRequest);
        MockHttpServletResponse response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            req.getReader().lines().forEach(line -> {
            });

            ((HttpServletResponse) res).setStatus(200);
            res.getWriter().write("response-body");
        };

        filter.doFilter(request, response, chain);

        ILoggingEvent loggingEvent = logs.iterator().next();
        assertThat(loggingEvent.getFormattedMessage()).contains("test-body");
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void shouldLogResponseNormally() throws ServletException, IOException {
        List<ILoggingEvent> logs = logAppender.list;
        MockHttpServletRequest baseRequest = new MockHttpServletRequest("POST", "/fakeURL");
        baseRequest.setContent("test-body".getBytes());
        baseRequest.setCharacterEncoding("UTF-8");

        MockHttpServletResponse response = new MockHttpServletResponse();

        ContentCachingRequestWrapper request = new ContentCachingRequestWrapper(baseRequest);

        FilterChain chain = (req, res) -> {
            req.getReader().lines().forEach(line -> {
            });

            ((HttpServletResponse) res).setStatus(200);
            res.getWriter().write("response-body");
        };

        filter.doFilter(request, response, chain);

        ILoggingEvent loggingEvent = logs.iterator().next();
        assertThat(loggingEvent.getFormattedMessage()).contains("test-body");
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.INFO);
    }

    @Test
    void shouldLogErrorWhenExceptionOccurs() throws ServletException, IOException {
        FilterChain mockChain = mock(FilterChain.class);
        List<ILoggingEvent> logs = logAppender.list;

        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/error");
        request.setContent("fail".getBytes());
        request.setCharacterEncoding("UTF-8");

        MockHttpServletResponse response = new MockHttpServletResponse();

        doThrow(new RuntimeException("Simulated error")).when(mockChain)
                .doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));

        assertThatThrownBy(() -> filter.doFilter(request, response, mockChain)).isInstanceOf(RuntimeException.class).hasMessage("Simulated error");

        ILoggingEvent loggingEvent = logs.iterator().next();
        assertThat(loggingEvent.getLevel()).isEqualTo(Level.ERROR);
        assertThat(loggingEvent.getFormattedMessage()).contains("Exception during filter chain: Simulated error");
    }

    @Test
    void shouldMaskPasswordFieldsInRequestBody() throws Exception {
        String body = """
                {
                    "username": "john.doe",
                    "oldPassword": "qwerty1234",
                    "newPassword": "qwerty9999"
                }
                """;

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/change-password");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        request.setCharacterEncoding("UTF-8");

        Assertions.assertNotNull(request.getContentAsByteArray());
        Assertions.assertNotNull(request.getCharacterEncoding());

        String requestBody = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
        TransactionLoggingFilter filter = new TransactionLoggingFilter();
        String maskedRequest = filter.maskSensitiveFields(requestBody);

        assertTrue(maskedRequest.contains("\"oldPassword\": \"*****\""));
        assertTrue(maskedRequest.contains("\"newPassword\": \"*****\""));
        assertFalse(maskedRequest.contains("qwerty1234"));
        assertFalse(maskedRequest.contains("qwerty9999"));
    }

    @Test
    void shouldMaskPasswordFieldsInResponseBody() throws Exception {
        String body = """
                {
                    "username": "john.doe",
                    "oldPassword": "qwerty1234",
                    "newPassword": "qwerty9999"
                }
                """;

        MockHttpServletRequest request = new MockHttpServletRequest("PUT", "/api/v1/change-password");
        request.setContent(body.getBytes(StandardCharsets.UTF_8));
        request.setCharacterEncoding("UTF-8");

        MockHttpServletResponse response = new MockHttpServletResponse();
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        FilterChain chain = (req, res) -> {
            res.setContentType("application/json");
            PrintWriter writer = res.getWriter();
            writer.write(body);
            writer.flush();
        };

        TransactionLoggingFilter filter = new TransactionLoggingFilter();
        filter.doFilterInternal(request, wrappedResponse, chain);

        String responseBody = new String(wrappedResponse.getContentAsByteArray(), wrappedResponse.getCharacterEncoding());
        String maskedResponse = filter.maskSensitiveFields(responseBody);

        wrappedResponse.copyBodyToResponse();

        assertTrue(maskedResponse.contains("\"oldPassword\": \"*****\""));
        assertTrue(maskedResponse.contains("\"newPassword\": \"*****\""));
        assertFalse(maskedResponse.contains("qwerty1234"));
        assertFalse(maskedResponse.contains("qwerty9999"));
    }
}
