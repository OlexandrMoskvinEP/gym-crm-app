package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.domain.model.User;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Optional;

import static com.gym.crm.app.data.mapper.UserMapper.constructUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserRepositoryImplTest {
    private static AnnotationConfigApplicationContext context;
    private static UserRepositoryImpl userRepository;

    @BeforeAll
    static void setUp() {
        System.setProperty("env", "test");
        context = new AnnotationConfigApplicationContext(AppConfig.class);
        userRepository = context.getBean(UserRepositoryImpl.class);
    }

    @Test
    @Order(1)
    void shouldSaveAndFinedUser() {
        User userToSave = constructUser();

        User saved = userRepository.save(userToSave);

        Optional<User> foundedUser = userRepository.findByUsername(userToSave.getUsername());

        assertTrue(foundedUser.isPresent());
        assertNotNull(saved);

        assertEquals(userToSave, saved);
        assertEquals(userToSave, foundedUser.get());
        assertEquals(saved, foundedUser.get());
    }

    @Test
    @Order(2)
    void shouldUpdateAndFinedUser() {
        User existing = userRepository.findByUsername("Alice.Moro").orElseThrow();

        User updated = existing.toBuilder()
                .firstName("AliceUpdated")
                .password("newSecret123")
                .username("AliceUpdated.Moro")
                .build();

        userRepository.update(updated);

        Optional<User> found = userRepository.findByUsername(updated.getUsername());

        assertTrue(found.isPresent());
        assertEquals("AliceUpdated", found.get().getFirstName());
        assertNotEquals(existing, found.get());
    }

    @Test
    @Order(3)
    void shouldDeleteUserByUsername() {
        User existing = userRepository.findByUsername("AliceUpdated.Moro").orElseThrow();

        userRepository.deleteByUsername(existing.getUsername());

        Optional<User> found = userRepository.findByUsername(existing.getUsername());

        assertFalse(found.isPresent());
    }

    //todo добавить проверку викидання ексепшенов
    @AfterAll
    static void tearDown() {
        context.close();
    }
}