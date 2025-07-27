package com.gym.crm.app.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionLoggingFilter extends OncePerRequestFilter {
    private static final String TRANSACTION_ID = "transactionId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String transactionId = generateTransactionId();
        MDC.put(TRANSACTION_ID, transactionId);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
            log.info("Incoming request: {} {}", wrappedRequest, wrappedResponse);

            byte[] requestContent = wrappedRequest.getContentAsByteArray();
            if (requestContent.length > 0) {
                log.info("Request body: {}", new String(requestContent, request.getCharacterEncoding()));
            }

            log.info("Response status: {}", wrappedResponse.getStatus());

            byte[] responseContent = wrappedResponse.getContentAsByteArray();
            if (responseContent.length > 0) {
                log.info("Response body: {}", new String(responseContent, response.getCharacterEncoding()));
            }

            response.setHeader("X-Transaction-Id", transactionId);
        } finally {
            wrappedResponse.copyBodyToResponse();
            MDC.remove(TRANSACTION_ID);
        }
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
