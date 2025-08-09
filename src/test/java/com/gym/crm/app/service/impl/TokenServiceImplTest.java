package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.model.RefreshToken;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.security.jwt.JwtTokenProvider;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.service.RefreshTokenService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceImplTest {
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private AuthenticationService authenticationService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private TraineeRepository traineeRepository;
    @Mock
    private TrainerRepository trainerRepository;

    @InjectMocks
    private TokenServiceImpl tokenService;

    @Test
    void login_ShouldReturnTokens() {
        LoginRequest loginRequest = new LoginRequest("john", "secret");
        AuthenticatedUser user = AuthenticatedUser.builder().userId(1L).username("john").build();
        RefreshToken refreshToken = RefreshToken.builder().token("raw-refresh").build();

        when(authenticationService.getAuthenticatedUser(loginRequest)).thenReturn(user);
        when(jwtTokenProvider.generateToken(user)).thenReturn("access");
        when(jwtTokenProvider.generateRefreshToken(1L, Duration.ofDays(14))).thenReturn(refreshToken);
        when(refreshTokenService.saveRefreshToken(refreshToken, 1L)).thenReturn("raw-refresh");

        JwtTokenResponse resp = tokenService.login(loginRequest);

        assertEquals("access", resp.getAccessToken());
        assertEquals("raw-refresh", resp.getRefreshToken());
    }

    @Test
    void refresh_ShouldReturnNewTokens() {
        String oldRefresh = "old-refresh";
        Long userId = 1L;
        User userEntity = User.builder().username("john.connor").build();

        AuthenticatedUser authUser = AuthenticatedUser.builder().username("john.connor").build();
        RefreshToken newRefresh = RefreshToken.builder().token("new-raw").build();

        when(refreshTokenService.validateAndGetUserId(oldRefresh)).thenReturn(Optional.of(userId));
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));
        when(trainerRepository.findByUserUsername("john.connor")).thenReturn(Optional.of(new Trainer()));
        when(userMapper.toAuthenticatedUser(userEntity)).thenReturn(authUser);
        when(jwtTokenProvider.generateToken(any())).thenReturn("new-access");
        when(jwtTokenProvider.generateRefreshToken(eq(userId), any())).thenReturn(newRefresh);
        when(refreshTokenService.saveRefreshToken(newRefresh, userId)).thenReturn("new-raw");

        JwtTokenResponse resp = tokenService.refresh(oldRefresh);

        assertEquals("new-access", resp.getAccessToken());
        assertEquals("new-raw", resp.getRefreshToken());
        verify(refreshTokenService).delete(oldRefresh);
    }
}