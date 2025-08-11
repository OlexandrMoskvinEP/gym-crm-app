package com.gym.crm.app.security.jwt;

import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.security.AuthenticatedUserService;
import com.gym.crm.app.security.UserRole;
import com.gym.crm.app.security.model.AuthenticatedUser;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticatedUserService authenticatedUserService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String authorizationHeader = request.getHeader("Authorization");

            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authorizationHeader.substring(7);
            AuthenticatedUser user = jwtTokenProvider.parseToken(token);

            Boolean isActive = user.getIsActive();
            if (isActive != null && !isActive) {
                throw new UnacceptableOperationException("User is not active, any operations not allowed");
            }

            UserRole role = authenticatedUserService.resolveUserRole(user.getUsername());
            user = user.toBuilder().build();

            String springRole = role.name().startsWith("ROLE_") ? role.name() : "ROLE_" + role.name();
            List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(springRole));

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, authorities);

            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("JWT authentication successfully for user: {} with role {}", user.getUsername(), user.getRole());

            filterChain.doFilter(request, response);

        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT authentication failed: {}", e.getMessage());

            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
        }
    }
}
