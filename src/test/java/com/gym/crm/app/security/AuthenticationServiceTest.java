package com.gym.crm.app.security;

import com.gym.crm.app.domain.dto.user.UserCredentialsDto;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Mock
    private UserRepository userRepository;
    @Mock
    private CurrentUserHolder currentUserHolder;

    private AuthenticationService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new AuthenticationService(userRepository, passwordEncoder, currentUserHolder);
    }

    @Test
    void shouldAuthenticateWithRealEncodedPassword() {
        String plainPassword = "qwerty123";
        String encodedPassword = passwordEncoder.encode(plainPassword);

        User user = User.builder()
                .username("john.doe")
                .password(encodedPassword)
                .build();

        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(user));

        UserCredentialsDto correctCredentials = new UserCredentialsDto("john.doe", plainPassword);
        UserCredentialsDto wrongCredentials = new UserCredentialsDto("john.doe", "qwerty321");

        assertDoesNotThrow(() -> service.authenticate(correctCredentials));
        verify(currentUserHolder).set(user);

        assertThrows(SecurityException.class, () -> service.authenticate(wrongCredentials));
        verify(currentUserHolder, times(1)).set(user);
    }
}