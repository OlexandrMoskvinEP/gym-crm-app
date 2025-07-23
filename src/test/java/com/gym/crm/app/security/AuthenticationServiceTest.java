package com.gym.crm.app.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationException;
import com.gym.crm.app.mapper.UserMapper;
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
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String USERNAME = "maximus.cena";
    private static final String PLAIN_PASSWORD = "PASSWORD123";
    private static final String INVALID_PASSWORD = "PASSWORD321";
    private static final String ENCODED_PASSWORD = "gfjSl34so$#1";
    private static final String USER_ROLE = UserRole.ADMIN.name();

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserHolder currentUserHolder;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthenticationService service;

    @Test
    @DisplayName("should successfully authenticate user if credentials are correct")
    void shouldSuccessfullyAuthenticateUserIfCredentialsAreCorrect() {
        User user = buildUserWithPassword();
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(userMapper.toAuthenticatedUser(user)).thenReturn(authenticatedUser);
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertDoesNotThrow(() -> service.authorisationFilter(correctCredentials));
        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if password is wrong")
    void shouldThrowExceptionIfPasswordIsWrong() {
        User user = buildUserWithPassword();
        UserCredentialsDto wrongCredentials = new UserCredentialsDto(USERNAME, INVALID_PASSWORD, USER_ROLE);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(INVALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        RuntimeException exception = assertThrows(AuthentificationException.class, () -> service.authorisationFilter(wrongCredentials));

        assertEquals("User cannot be authenticated - invalid credentials", exception.getMessage());
        verifyNoInteractions(currentUserHolder);
    }

    @Test
    @DisplayName("should throw exception if user is not found for provided username")
    void shouldThrowExceptionIfUserIsNotFoundForProvidedUsername() {
        UserCredentialsDto userCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(AuthentificationException.class, () -> service.authorisationFilter(userCredentials));

        assertEquals("User with such username does not exist", exception.getMessage());
        verifyNoInteractions(currentUserHolder);
    }

    @Test
    @DisplayName("should log if user was successfully authenticated")
    void shouldLogIfUserWasSuccessfullyAuthenticated() {
        User user = buildUserWithPassword();
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationService.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(userMapper.toAuthenticatedUser(user)).thenReturn(authenticatedUser);
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertDoesNotThrow(() -> service.authorisationFilter(correctCredentials));

        Optional<ILoggingEvent> actualEvent = appender.getLogs().stream().findFirst();

        assertTrue(actualEvent.isPresent());
        assertTrue(actualEvent.get().getFormattedMessage()
                .contains("User [maximus.cena] authorise successfully"));
        assertEquals("INFO", actualEvent.get().getLevel().toString());
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