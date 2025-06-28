package com.gym.crm.app.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

class PasswordGeneratorTest {
    private PasswordGenerator passwordGenerator;

    @BeforeEach
    void setUp() {
        passwordGenerator = new PasswordGenerator();
        passwordGenerator.setRandom(new SecureRandom());
    }

    @Test
    void shouldGenerateSequentialTraineeId() {
        int id1 = passwordGenerator.generateTrainerId();
        int id2 = passwordGenerator.generateTrainerId();

        assertEquals(id1 + 1, id2);
    }

    @Test
    void shouldGenerateSequentialTrainerId() {
        int id1 = passwordGenerator.generateTraineeId();
        int id2 = passwordGenerator.generateTraineeId();

        assertEquals(id1 + 1, id2);
    }

    @Test
    void shouldGenerateUniqPassword() {
        String password = passwordGenerator.generatePassword();
        String password2 = passwordGenerator.generatePassword();
        String password3 = passwordGenerator.generatePassword();

        assertNotEquals(password, password2);
        assertNotEquals(password2, password3);
        assertNotEquals(password, password3);
    }

    @Test
    void shouldGenerateCorrectUsername() {
        String firstName = "firstName";
        String lastName = "lastName";
        String expected = "firstName.lastName";

        assertEquals(expected, passwordGenerator.generateUsername(firstName, lastName));
    }

    @Test
    void shouldAddIndexWhenDuplicate() {
        String username = "username";
        int index = 123;
        String expected = "username123";

        assertEquals(expected, passwordGenerator.addIndexWhenDuplicate(username, index));
    }
}