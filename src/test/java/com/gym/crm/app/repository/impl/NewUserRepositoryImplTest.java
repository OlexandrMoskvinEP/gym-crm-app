package com.gym.crm.app.repository.impl;


import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/users-data.xml", cleanBefore = true, cleanAfter = true)
public class NewUserRepositoryImplTest extends RepositoryIntegrationTest {

    @Test
    void shouldReturnAllUsers() {
        List<User> actual = userRepository.findAll();
        assertEquals(6, actual.size());
    }

    @ParameterizedTest
    @ValueSource(strings = {"arnold.schwarzenegger", "irina.petrova", "john.smith"})
    void shouldFindByUsername(String username) {
        Optional<User> actual = userRepository.findByUsername(username);

         assertTrue(actual.isPresent());
    }

    @Test
    @Order(3)
    void shouldSaveEntity() {
        User toSave = constructUser();

        User saved = userRepository.save(toSave);
        User found = userRepository.findByUsername(toSave.getUsername()).orElse(null);

        assertNotNull(found);
        assertEquals(toSave.getUsername(), saved.getUsername());
    }

    @Test
    @Order(5)
    void shouldDeleteEntityByUsername() {
        String username = "irina.petrova";
        userRepository.deleteByUsername(username);
        assertFalse(userRepository.findByUsername(username).isPresent());
    }

    private static final AtomicInteger counter = new AtomicInteger(0);

    private static User constructUser() {
        int count = counter.incrementAndGet();
        return User.builder()
                .firstName("Test" + count)
                .lastName("User")
                .username("test.user" + count)
                .password("testPass" + count)
                .isActive(true)
                .build();
    }
}
