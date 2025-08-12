package com.gym.crm.app.security.jwt;

import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.security.service.AuthenticatedUserService;
import com.gym.crm.app.security.model.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static com.gym.crm.app.security.model.UserRole.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    private final AuthenticatedUser mockUser = getAuthenticatedUser();
    private final AuthenticatedUser mockNotActiveUser = getNotActiveAuthenticatedUser();

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthenticatedUserService authenticatedUserService;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    @Test
    void shouldAuthenticateWhenValidToken() throws Exception {
        String jwt = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.parseToken(jwt)).thenReturn(mockUser);
        when(authenticatedUserService.resolveUserRole(anyString())).thenReturn(ADMIN);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(mockUser, authentication.getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldRejectInvalidToken() throws Exception {
        String jwt = "bad.jwt.token";

        when(request.getHeader("Authorization")).thenReturn(" " + jwt);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldThrowExceptionWhenValidTokenButUserIsNotActive() throws Exception {
        String jwt = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.parseToken(jwt)).thenReturn(mockNotActiveUser);

        assertThrows(UnacceptableOperationException.class,
                () -> filter.doFilterInternal(request, response, filterChain));

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain, never()).doFilter(request, response);
    }

    private static AuthenticatedUser getAuthenticatedUser() {
        return AuthenticatedUser.builder()
                .userId(1L)
                .username("test.user")
                .role(ADMIN)
                .isActive(true)
                .build();
    }

    private static AuthenticatedUser getNotActiveAuthenticatedUser() {
        return AuthenticatedUser.builder()
                .userId(1L)
                .username("test.user")
                .role(ADMIN)
                .isActive(false)
                .build();
    }
}