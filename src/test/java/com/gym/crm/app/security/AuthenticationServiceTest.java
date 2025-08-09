package com.gym.crm.app.security;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.AuthentificationErrorException;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.jwt.JwtTokenProvider;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserCredentialsDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

import static com.gym.crm.app.security.UserRole.ADMIN;
import static com.gym.crm.app.security.UserRole.TRAINEE;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private static final String USERNAME = "maximus.cena";
    private static final String PLAIN_PASSWORD = "PASSWORD123";
    private static final String INVALID_PASSWORD = "PASSWORD321";
    private static final String ENCODED_PASSWORD = "gfjSl34so$#1";
    private static final String USER_ROLE = ADMIN.name();
    private static final User PLAIN_USER = buildUserWithPassword();
    private static final LoginRequest LOGIN_REQUEST = new LoginRequest(USERNAME, PLAIN_PASSWORD);
    private static final LoginRequest WRONG_LOGIN_REQUEST = new LoginRequest(USERNAME, INVALID_PASSWORD);
    private static final String TEST_JWT_TOKEN = "test.jwt.token";

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserHolder currentUserHolder;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private TrainerRepository trainerRepository;
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("should successfully allow user if credentials are correct")
    void shouldSuccessfullyAllowUserIfCredentialsAreCorrect() {
        User user = buildUserWithPassword().toBuilder().password(PLAIN_PASSWORD).build();
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(traineeRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainee()));
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertDoesNotThrow(() -> authenticationService.checkUserAuthorisation(correctCredentials, TRAINEE));

        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if password is wrong")
    void shouldThrowExceptionIfPasswordIsWrong() {
        User user = buildUserWithPassword();
        UserCredentialsDto wrongCredentials = new UserCredentialsDto(USERNAME, INVALID_PASSWORD, USER_ROLE);

        when(currentUserHolder.get()).thenReturn(AuthenticatedUser.builder().username(USERNAME).build());
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        RuntimeException exception = assertThrows(AuthorizationErrorException.class,
                () -> authenticationService.checkUserAuthorisation(wrongCredentials, ADMIN));

        assertEquals("User cannot be authenticated - invalid password", exception.getMessage());

        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if user is not logged in")
    void shouldThrowExceptionIfUserIsNotLoggedIn() {
        UserCredentialsDto userCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        RuntimeException exception = assertThrows(UnacceptableOperationException.class,
                () -> authenticationService.checkUserAuthorisation(userCredentials, ADMIN));

        assertEquals("User is not logged in", exception.getMessage());

        verify(currentUserHolder).get();
    }

    @Test
    @DisplayName("should throw exception if user was successfully authenticated but not allowed")
    void shouldThrowExceptionIfUserWasSuccessfullyAuthenticatedButNotAllowed() {
        User user = buildUserWithPassword();
        AuthenticatedUser authenticatedUser = buildAuthenticatedUser(user);
        UserCredentialsDto correctCredentials = new UserCredentialsDto(USERNAME, PLAIN_PASSWORD, USER_ROLE);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));
        when(currentUserHolder.get()).thenReturn(authenticatedUser);

        assertThrows(AuthorizationErrorException.class, () -> authenticationService.checkUserAuthorisation(correctCredentials, ADMIN));

        verify(currentUserHolder).get();
    }

    @Test
    void shouldSuccessfullyAuthoriseIfCorrectCredentials() {
        AtomicReference<String> actual = new AtomicReference<>();

        when(jwtTokenProvider.generateToken(any())).thenReturn(TEST_JWT_TOKEN);
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(PLAIN_USER));
        when(trainerRepository.findByUserUsername(USERNAME)).thenReturn(Optional.of(new Trainer()));
        when(passwordEncoder.matches(PLAIN_PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
        when(userMapper.toAuthenticatedUser(PLAIN_USER)).thenReturn(AuthenticatedUser.builder().username(USERNAME).build());

        assertDoesNotThrow(() -> actual.set(authenticationService.authenticate(LOGIN_REQUEST)), "Invalid username or password");

        assertEquals(TEST_JWT_TOKEN, actual.get());
        verify(currentUserHolder).set(any(AuthenticatedUser.class));
    }

    @Test
    void shouldNotAuthoriseIfWrongCredentials() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(PLAIN_USER));

        assertThrows(AuthentificationErrorException.class, () -> authenticationService.authenticate(WRONG_LOGIN_REQUEST),
                "Invalid password");
        verify(currentUserHolder, never()).set(any(AuthenticatedUser.class));
    }

    private static User buildUserWithPassword() {
        return User.builder()
                .username(USERNAME)
                .password(ENCODED_PASSWORD)
                .isActive(true)
                .build();
    }

    private AuthenticatedUser buildAuthenticatedUser(User user) {
        return AuthenticatedUser.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .isActive(user.getIsActive())
                .role(UserRole.valueOf(USER_ROLE)).build();
    }
}