package com.gym.crm.app.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class TransactionLoggingFilterTest {

    @Test
    void shouldGenerateTransactionIdAndAddHeader() throws ServletException, IOException {
        TransactionLoggingFilter filter = new TransactionLoggingFilter();
        FilterChain chain = mock(FilterChain.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        ArgumentCaptor<String> headerNameCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> headerValueCaptor = ArgumentCaptor.forClass(String.class);

        filter.doFilterInternal(request, response, chain);

        verify(response).setHeader(headerNameCaptor.capture(), headerValueCaptor.capture());

        assertThat(headerNameCaptor.getValue()).isEqualTo("X-Transaction-Id");
        assertThat(UUID.fromString(headerValueCaptor.getValue())).isInstanceOf(UUID.class);
        assertThat(MDC.get("transactionId")).isNull();
        verify(chain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
                any(ContentCachingResponseWrapper.class));
    }
}