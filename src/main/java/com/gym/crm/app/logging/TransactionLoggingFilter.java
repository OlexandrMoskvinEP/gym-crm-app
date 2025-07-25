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

        try {
            log.info("Incoming request: {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);

            log.info("Response status: {}", response.getStatus());

            response.setHeader("X-Transaction-Id", transactionId);
        } finally {
            MDC.remove(TRANSACTION_ID);
        }
    }

    private String generateTransactionId() {
        return UUID.randomUUID().toString();
    }
}
