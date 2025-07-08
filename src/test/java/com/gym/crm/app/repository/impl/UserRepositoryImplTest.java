package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserRepositoryImplTest extends RepositoryIntegrationTest {
    private final List<User> allUsers = data.getTestUsers();

    @Test
    void shouldReturnAllUsers() {
        List<User> actual = userRepository.findAll();

        assertFalse(actual.isEmpty());
        assertTrue(actual.containsAll(allUsers));
    }

    @ParameterizedTest
    @ValueSource(strings = {"arnold.schwarzenegger", "irina.petrova", "john.smith"})
    void shouldFindByUsername(String username) {
        Optional<User> expected = allUsers.stream().filter(user -> user.getUsername().equals(username)).findFirst();
        Optional<User> actual = userRepository.findByUsername(username);

        assertFalse(actual.isEmpty());
        assertEquals(expected.get(), actual.get());
    }

    @Test
    void shouldSaveEntity() {
        User toSave = constructUser(1);

        User saved = userRepository.save(toSave);
        User founded = userRepository.findByUsername(toSave.getUsername()).orElse(null);

        assertEquals(toSave, saved);
        assertNotNull(founded);
        assertEquals(toSave, founded);
    }

    @Test
    void shouldUpdate() {
        User toUpdate = constructUser(2);
        userRepository.update(toUpdate);

        User founded = userRepository.findByUsername(toUpdate.getUsername()).orElse(null);

        assertNotNull(founded);

        assertEquals(toUpdate.getFirstName(), founded.getFirstName());
        assertEquals(toUpdate.getLastName(), founded.getLastName());
        assertEquals(toUpdate.getPassword(), founded.getPassword());
        assertEquals(toUpdate.isActive(), founded.isActive());
    }

    @Test
    void shouldDeleteEntityByUsername() {
        User toDelete = constructUser(3);

        userRepository.save(toDelete);
        userRepository.deleteByUsername(toDelete.getUsername());
        userRepository.deleteByUsername(toDelete.getUsername());

        User founded = userRepository.findByUsername(toDelete.getUsername()).orElse(null);

        assertNull(founded);
    }

    private static User constructUser(int index) {
        if (index == 1) {
            return User.builder()
                    .firstName("Alice")
                    .lastName("Moro")
                    .username("Alice.Moro")
                    .password("Abc123!@#")
                    .isActive(true)
                    .build();

        } else if (index == 2) {
            return User.builder()
                    .firstName("Bingo")
                    .lastName("Bongo")
                    .username("Bingo.Bongo")
                    .password("Abc123!@#")
                    .isActive(true)
                    .build();

        } else return User.builder()
                .firstName("Papa")
                .lastName("Carlo")
                .username("Papa.Carlo")
                .password("Abc123!@#")
                .isActive(true)
                .build();
    }
}
