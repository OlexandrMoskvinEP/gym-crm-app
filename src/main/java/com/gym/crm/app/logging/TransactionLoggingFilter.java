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
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionLoggingFilter extends OncePerRequestFilter {
    private static final Set<String> SENSITIVE_FIELDS = Set.of("password", "oldPassword", "newPassword");
    private static final Pattern SENSITIVE_PATTERN = Pattern.compile("(?i)(\"(%s)\"\\s*:\\s*\")([^\"]+)(\")"
            .formatted(String.join("|", SENSITIVE_FIELDS)));

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
            logRequestAndResponse(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            log.error("Exception during filter chain: {}", ex.getMessage(), ex);
            throw ex;
        } finally {
            wrappedResponse.copyBodyToResponse();
            response.setHeader("X-Transaction-Id", transactionId);
            MDC.remove(TRANSACTION_ID);
        }
    }

    private void logRequestAndResponse(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        String requestBody = new String(request.getContentAsByteArray(), request.getCharacterEncoding());
        requestBody = maskSensitiveFields(requestBody);
        String endpoint = String.format("%s %s", request.getMethod(), request.getRequestURI());
        log.info("Endpoint: {}, Request body: {}", endpoint, requestBody);

        int status = response.getStatus();
        log.info("Response status: {}", status);

        byte[] responseContent = response.getContentAsByteArray();
        if (responseContent.length > 0) {
            String responseBody = new String(responseContent, response.getCharacterEncoding());
            responseBody = maskSensitiveFields(responseBody);
            log.info("Response body: {}", responseBody);
        }
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }

    private String maskSensitiveFields(String body) {
        return SENSITIVE_PATTERN.matcher(body).replaceAll("$1*****$4");
    }
}
