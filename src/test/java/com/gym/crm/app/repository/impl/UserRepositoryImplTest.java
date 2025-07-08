package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/users.xml", cleanBefore = true, cleanAfter = true)
public class UserRepositoryImplTest extends RepositoryIntegrationTest {
    private final List<User> expected = data.getTestUsers();

    @Test
    void shouldReturnAllUsers() {
        List<User> actual = userRepository.findAll();

        assertFalse(actual.isEmpty());
        assertTrue(actual.containsAll(expected));
    }

    @ParameterizedTest
    @ValueSource(strings = {"arnold.schwarzenegger", "irina.petrova", "john.smith"})
    void shouldFindByUsername(String username) {
        Optional<User> expected = this.expected
                .stream().filter(user -> user.getUsername().equals(username)).findFirst();
        Optional<User> actual = userRepository.findByUsername(username);

        assertFalse(actual.isEmpty());

        assertEquals(expected.get().getLastName(), actual.get().getLastName());
        assertEquals(expected.get().getFirstName(), actual.get().getFirstName());
        assertEquals(expected.get().getPassword(), actual.get().getPassword());
        assertEquals(expected.get().isActive(), actual.get().isActive());
    }

    @Test
    void shouldSaveEntity() {
        User toSave = constructUser();

        User saved = userRepository.save(toSave);
        User founded = userRepository.findByUsername(toSave.getUsername()).orElse(null);

        assertEquals(toSave, saved);
        assertNotNull(founded);
        assertEquals(toSave, founded);
    }

    @Test
    void shouldUpdate() {
        User toUpdate = constructUser();
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
        User toDelete = constructUser();

        userRepository.save(toDelete);
        userRepository.deleteByUsername(toDelete.getUsername());
        userRepository.deleteByUsername(toDelete.getUsername());

        User founded = userRepository.findByUsername(toDelete.getUsername()).orElse(null);

        assertNull(founded);
    }

    private static User constructUser() {
        int count = new Random().nextInt(1000);

        return User.builder()
                .firstName("Trainee" + count)
                .lastName("Test")
                .username("trainee" + count)
                .password("Pwd123!@#")
                .isActive(true)
                .build();
    }
}
