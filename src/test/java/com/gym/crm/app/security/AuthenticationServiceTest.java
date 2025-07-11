package com.gym.crm.app.security;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.gym.crm.app.domain.dto.user.UserCredentialsDto;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.UserRepository;
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

import static ch.qos.logback.classic.Level.INFO;
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
    private static final String INVALID_PASSWORD = "PASSWORD123";
    private static final String ENCODED_PASSWORD = "gfjSl34so$#1";

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserHolder currentUserHolder;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthenticationService service;

    @Test
    @DisplayName("should successfully authenticate user if credentials are correct")
    void shouldSuccessfullyAuthenticateUserIfCredentialsAreCorrect() {
        User user = buildUserWithPassword(ENCODED_PASSWORD);
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertDoesNotThrow(() -> service.authenticate(correctCredentials));

        verify(currentUserHolder).set(user);
    }

    @Test
    @DisplayName("should throw exception if password is wrong")
    void shouldThrowExceptionIfPasswordIsWrong() {
        User user = buildUserWithPassword(ENCODED_PASSWORD);
        UserCredentialsDto wrongCredentials = new UserCredentialsDto(USERNAME, INVALID_PASSWORD);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(INVALID_PASSWORD, ENCODED_PASSWORD)).thenReturn(false);

        SecurityException ex = assertThrows(SecurityException.class, () -> service.authenticate(wrongCredentials));

        assertEquals("User cannot be authenticated - invalid credentials", ex.getMessage());
        verifyNoInteractions(currentUserHolder);
    }

    @Test
    @DisplayName("should throw exception if user is not found for provided username")
    void shouldThrowExceptionIfUserIsNotFoundForProvidedUsername() {
        User user = buildUserWithPassword(ENCODED_PASSWORD);
        UserCredentialsDto userCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        SecurityException ex = assertThrows(SecurityException.class, () -> service.authenticate(userCredentials));

        assertEquals("User with such username does not exist", ex.getMessage());
        verifyNoInteractions(currentUserHolder);
    }

    @Test
    @DisplayName("should log if user was successfully authenticated")
    void shouldLogIfUserWasSuccessfullyAuthenticated() {
        User user = buildUserWithPassword(ENCODED_PASSWORD);
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD);

        Logger logger = (Logger) LoggerFactory.getLogger(AuthenticationService.class);
        InMemoryLogAppender appender = new InMemoryLogAppender();
        appender.setContext((LoggerContext) LoggerFactory.getILoggerFactory());
        appender.start();
        logger.addAppender(appender);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);

        assertDoesNotThrow(() -> service.authenticate(correctCredentials));

        Optional<ILoggingEvent> actualEvent = appender.getLogs().stream().findFirst();
        assertTrue(actualEvent.isPresent());
        assertTrue(actualEvent.get().getFormattedMessage()
                .contains("User [maximus.cena] authenticated successfully"));
        assertEquals(INFO, actualEvent.get().getLevel());
        verify(currentUserHolder).set(user);
    }

    private User buildUserWithPassword(String password) {
        return User.builder()
                .username(USERNAME)
                .password(password)
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
}