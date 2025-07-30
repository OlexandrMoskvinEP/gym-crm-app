package com.gym.crm.app.service.common;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
    @Mock
    SecureRandom mockRandom;
    @Spy
    BCryptPasswordEncoder encoder;

    @InjectMocks
    private PasswordService passwordService;

    @Test
    void shouldGeneratePasswordOfFixedLength() {
        String password = passwordService.generatePassword();

        assertThat(password).isNotNull();
        assertThat(password).hasSize(10);
    }

    @Test
    void shouldEncodePasswordCorrectly() {
        String rawPassword = "qwerty123";

        String encoded = passwordService.encodePassword(rawPassword);

        assertThat(encoded).isNotNull();
        assertThat(encoded).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encoded)).isTrue();
    }
}