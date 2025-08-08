package com.gym.crm.app.security.jwt;

import com.gym.crm.app.security.CurrentUserHolder;
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

import static com.gym.crm.app.security.UserRole.ADMIN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private CurrentUserHolder currentUserHolder;
    @Mock
    private FilterChain filterChain;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @InjectMocks
    private JwtAuthenticationFilter filter;

    private final AuthenticatedUser mockUser = getAuthenticatedUser();

    @Test
    void shouldAuthenticateWhenValidToken() throws Exception {
        String jwt = "valid.jwt.token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + jwt);
        when(jwtTokenProvider.parseToken(jwt)).thenReturn(mockUser);

        filter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        assertNotNull(authentication);
        assertEquals(mockUser, authentication.getPrincipal());
        verify(currentUserHolder).clear();
        verify(filterChain).doFilter(request, response);
    }

    private static AuthenticatedUser getAuthenticatedUser() {
        return AuthenticatedUser.builder()
                .userId(1L)
                .username("test.user")
                .role(ADMIN)
                .isActive(true)
                .build();
    }
}