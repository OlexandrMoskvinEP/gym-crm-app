package com.gym.crm.app.security.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserRole;
import com.gym.crm.app.security.service.LoginAttemptsService;
import com.gym.crm.app.security.service.impl.AuthenticatedUserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticatedUserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private LoginAttemptsService loginAttemptsService;
    @InjectMocks
    private AuthenticatedUserServiceImpl authenticatedUserService;

    @Test
    void shouldReturnAuthenticatedUser() {
        LoginRequest loginRequest = new LoginRequest("john", "secret");
        User userEntity = User.builder().username("john").password("hashed-pass").build();

        AuthenticatedUser mappedUser = AuthenticatedUser.builder()
                .username("john")
                .password("hashed-pass")
                .role(UserRole.TRAINER)
                .isActive(true)
                .build();

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(userEntity));
        when(passwordEncoder.matches("secret", "hashed-pass")).thenReturn(true);
        when(userMapper.toAuthenticatedUser(userEntity)).thenReturn(mappedUser);
        when(trainerRepository.findByUserUsername("john")).thenReturn(Optional.of(new Trainer()));
        when(loginAttemptsService.isBlocked(anyString())).thenReturn(false);

        AuthenticatedUser actual = authenticatedUserService.getAuthenticatedUser(loginRequest);

        assertNotNull(actual);
        assertEquals("john", actual.getUsername());
        assertEquals(UserRole.TRAINER, actual.getRole());

        verify(userRepository).findByUsername("john");
        verify(passwordEncoder).matches("secret", "hashed-pass");
        verify(userMapper).toAuthenticatedUser(userEntity);
    }

    @Test
    void shouldResolveUserRole() {
        String username = "john.connor";

        when(trainerRepository.findByUserUsername(username)).thenReturn(Optional.empty());
        when(traineeRepository.findByUserUsername(username)).thenReturn(Optional.of(Trainee.builder().build()));

        UserRole actual = authenticatedUserService.resolveUserRole(username);

        assertNotNull(actual);
        assertEquals(UserRole.TRAINEE, actual);
    }
}