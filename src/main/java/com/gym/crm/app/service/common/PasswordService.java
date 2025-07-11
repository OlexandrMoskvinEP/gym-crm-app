package com.gym.crm.app.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@RequiredArgsConstructor
@Component
public class PasswordService {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
    private static final int PASSWORD_LENGTH = 10;

    private BCryptPasswordEncoder encoder;
    private SecureRandom random;

    @Autowired
    public void setRandom(SecureRandom random) {
        this.encoder = new BCryptPasswordEncoder();
        this.random = random;
    }

    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    public String encodePassword(String source) {
        return encoder.encode(source);
    }
}
