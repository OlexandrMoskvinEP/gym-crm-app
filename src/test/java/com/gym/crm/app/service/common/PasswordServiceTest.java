package com.gym.crm.app.service.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class PasswordServiceTest {
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    private PasswordService passwordService;
    
    @BeforeEach
    void setUp() {
        SecureRandom mockRandom = Mockito.mock(SecureRandom.class);
        passwordService = new PasswordService();
        passwordService.setRandom(mockRandom);

        when(mockRandom.nextInt(Mockito.anyInt())).thenReturn(0);
    }

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