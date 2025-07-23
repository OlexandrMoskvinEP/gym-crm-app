package com.gym.crm.app.security;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserCredentialsDto;
import lombok.Getter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.gym.crm.app.security.UserRole.ADMIN;
import static com.gym.crm.app.security.UserRole.TRAINEE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String USERNAME = "maximus.cena";
    private static final String PLAIN_PASSWORD = "PASSWORD123";
    private static final String INVALID_PASSWORD = "PASSWORD321";
    private static final String ENCODED_PASSWORD = "gfjSl34so$#1";
    private static final String USER_ROLE = ADMIN.name();

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserHolder currentUserHolder;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    TraineeRepository traineeRepository;
    @InjectMocks
    private AuthenticationService service;

    @Test
    @DisplayName("should successfully allow user if credentials are correct")
    void shouldSuccessfullyAllowUserIfCredentialsAreCorrect() {
        User user = buildUserWithPassword();
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(traineeRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new Trainee()));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertDoesNotThrow(() -> service.authorisationFilter(correctCredentials, TRAINEE));
        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if password is wrong")
    void shouldThrowExceptionIfPasswordIsWrong() {
        User user = buildUserWithPassword();
        UserCredentialsDto wrongCredentials = new UserCredentialsDto(USERNAME, INVALID_PASSWORD, USER_ROLE);

        when(currentUserHolder.get()).thenReturn(AuthenticatedUser.builder().username(USERNAME).build());
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(INVALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        RuntimeException exception = assertThrows(AuthorizationErrorException.class, () -> service.authorisationFilter(wrongCredentials, ADMIN));

        assertEquals("User cannot be authenticated - invalid password", exception.getMessage());
        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if user is not found for provided username")
    void shouldThrowExceptionIfUserIsNotFoundForProvidedUsername() {
        UserCredentialsDto userCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        RuntimeException exception = assertThrows(UnacceptableOperationException.class, () -> service.authorisationFilter(userCredentials, ADMIN));

        assertEquals("User cannot perform this operation on behalf of another user", exception.getMessage());
        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if user was successfully authenticated but not allowed")
    void shouldThrowExceptionIfUserWasSuccessfullyAuthenticatedButNotAllowed() {
        User user = buildUserWithPassword();
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(trainerRepository.findByUsername(USERNAME)).thenReturn(Optional.of(new Trainer()));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertThrows(AuthorizationErrorException.class, () -> service.authorisationFilter(correctCredentials, ADMIN));
        verify(currentUserHolder).get();
    }

    private User buildUserWithPassword() {
        return User.builder()
                .username(USERNAME)
                .password(AuthenticationServiceTest.ENCODED_PASSWORD)
                .build();
    }

    @Getter
    private static class InMemoryLogAppender extends AppenderBase<ILoggingEvent> {
        private final List<ILoggingEvent> logs = new ArrayList<>();

        @Override
        protected void append(ILoggingEvent eventObject) {
            logs.add(eventObject);
        }

    }

    private AuthenticatedUser buildAuthenticatedUser(User user) {
        return AuthenticatedUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .isActive(user.isActive())
                .role(UserRole.valueOf(USER_ROLE)).build();
    }
}