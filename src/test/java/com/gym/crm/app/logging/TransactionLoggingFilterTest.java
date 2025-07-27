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

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

        assertThat(logs).anyMatch(log -> log.getFormattedMessage().contains("Request body: test-body"));
    }

    @Test
    void shouldResponseNormally() throws ServletException, IOException {
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

        assertThat(logs).anyMatch(log -> log.getFormattedMessage().contains("Response status: 200"));
        assertThat(logs).anyMatch(log -> log.getFormattedMessage().contains("Response body: response-body"));
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

        assertThatThrownBy(() -> filter.doFilter(request, response, mockChain))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Simulated error");

        assertThat(logs)
                .anySatisfy(log -> {
                    assertThat(log.getLevel()).isEqualTo(Level.ERROR);
                    assertThat(log.getFormattedMessage()).contains("Exception during filter chain:");
                });
    }
}
