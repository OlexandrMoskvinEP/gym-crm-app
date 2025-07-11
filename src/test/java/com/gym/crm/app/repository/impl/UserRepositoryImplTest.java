package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/users.xml", cleanBefore = true, cleanAfter = true)
public class UserRepositoryImplTest extends AbstractRepositoryTest<UserRepository> {

    @Test
    void shouldReturnAllUsers() {
        List<User> expected = constructExpectedUsers();

        List<User> actual = repository.findAll();

        assertEquals(6, actual.size());
        assertTrue(actual.containsAll(expected));
    }

    @ParameterizedTest
    @MethodSource("provideUsersFromDataset")
    void shouldFindByUsername(String username, String firstName, String lastName, String password, boolean isActive) {
        Optional<User> actual = repository.findByUsername(username);

        assertTrue(actual.isPresent());
        User user = actual.get();

        assertEquals(firstName, user.getFirstName());
        assertEquals(lastName, user.getLastName());
        assertEquals(password, user.getPassword());
        assertEquals(isActive, user.isActive());
    }

    @Test
    void shouldSaveEntity() {
        User toSave = constructUser();

        User actual = repository.save(toSave);

        User found = repository.findByUsername(toSave.getUsername()).orElse(null);
        assertEquals(toSave, actual);
        assertNotNull(found);
        assertEquals(toSave, found);
    }

    @Test
    void shouldUpdate() {
        User existUser = repository.findByUsername("john.smith").orElseThrow();
        User toUpdate = existUser.toBuilder()
                .firstName("Updated")
                .lastName("Name")
                .password("newpwd123")
                .isActive(false)
                .build();

        repository.update(toUpdate);

        User updatedUser = repository.findByUsername("john.smith").orElseThrow();
        assertEquals("Updated", updatedUser.getFirstName());
        assertEquals("Name", updatedUser.getLastName());
        assertEquals("newpwd123", updatedUser.getPassword());
        assertFalse(updatedUser.isActive());
    }

    @Test
    void shouldDeleteEntityByUsername() {
        User toDelete = constructUser();

        repository.save(toDelete);
        repository.deleteByUsername(toDelete.getUsername());

        assertFalse(repository.findByUsername(toDelete.getUsername()).isPresent());
    }

    private static List<User> constructExpectedUsers() {
        List<User> expected = new ArrayList<>();

        expected.add(new User(1L, "John", "Smith", "john.smith",
                "$2a$10$I2hY4OUNaAjBqiSGEWOdgO0dFn3VD66TwS045G1E3dmcMcbgaJrAe", true));
        expected.add(new User(2L, "Olga", "Ivanova", "olga.ivanova",
                "$2a$10$DiCT7tTKoF3DXcnYp3SWCurgZirxne/b.WittVRFtGLeioBxhCUAe", true));
        expected.add(new User(3L, "Irina", "Petrova", "irina.petrova",
                "$2a$10$fGPZWu6oKw8UQ39rEwDVPOisLg8Cg/WBiWtKtlh/34L/dt31eKK7q", true));
        expected.add(new User(4L, "Boris", "Krasnov", "boris.krasnov",
                "$2a$10$Yu0rZBxEy8.jTwgMco4cZeXLRFOKGyniS/8cRHWFROB.30inAPb.C", true));
        expected.add(new User(5L, "Mykyta", "Solntcev", "mykyta.solntcev",
                "$2a$10$DAp7dSCT2aJ5fX79wgBOEeHG7PC4lsMwDzm3h5vn3qPOkkvG5bDe.", true));
        expected.add(new User(6L, "Arnold", "Schwarzenegger", "arnold.schwarzenegger",
                "$2a$10$QWKDqoCWbRs2bsDFL.NDE.orqzj06S0fZMuChNvnWOmPPFliKtOp6", true));

        return expected;
    }

    private static Stream<Arguments> provideUsersFromDataset() {
        return Stream.of(
                Arguments.of("john.smith", "John", "Smith", "$2a$10$I2hY4OUNaAjBqiSGEWOdgO0dFn3VD66TwS045G1E3dmcMcbgaJrAe", true),
                Arguments.of("irina.petrova", "Irina", "Petrova", "$2a$10$fGPZWu6oKw8UQ39rEwDVPOisLg8Cg/WBiWtKtlh/34L/dt31eKK7q", true),
                Arguments.of("arnold.schwarzenegger", "Arnold", "Schwarzenegger", "$2a$10$QWKDqoCWbRs2bsDFL.NDE.orqzj06S0fZMuChNvnWOmPPFliKtOp6", true)
        );
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