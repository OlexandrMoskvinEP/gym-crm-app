package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TraineeRepositoryImplTest extends RepositoryIntegrationTest {
    private static final AtomicInteger counter = new AtomicInteger(100);

    private final List<Trainee> allTrainees = data.getTestTrainees();

    @Test
    void shouldReturnAllTrainees() {
        List<Trainee> actual = traineeRepository.findAll();

        assertFalse(actual.isEmpty());
        assertTrue(actual.containsAll(allTrainees));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void shouldFindTraineeById(Long id) {
        Optional<Trainee> expected = allTrainees.stream().filter(trainee -> trainee.getId().equals(id)).findFirst();
        Optional<Trainee> actual = traineeRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getAddress(), actual.get().getAddress());
    }

    @ParameterizedTest
    @ValueSource(strings = {"john.smith", "olga.ivanova", "irina.petrova"})
    void shouldFindByUsername(String username) {
        Optional<Trainee> actual = traineeRepository.findByUsername(username);

        assertTrue(actual.isPresent());
        assertEquals(username, actual.get().getUser().getUsername());
    }

    @Test
    void shouldSaveEntity() {
        Trainee toSave = constructTrainee();

        Trainee saved = traineeRepository.save(toSave);
        Trainee found = traineeRepository.findByUsername(toSave.getUser().getUsername()).orElse(null);

        assertNotNull(saved);
        assertNotNull(found);
        assertEquals(toSave.getUser().getUsername(), found.getUser().getUsername());
    }

    @Test
    void shouldSaveAndUpdate() {
        Trainee toUpdate = constructTrainee();
        Trainee saved = traineeRepository.save(toUpdate);

        toUpdate = toUpdate.toBuilder()
                .id(saved.getId())
                .user(saved.getUser())
                .address("Updated Street")
                .dateOfBirth(LocalDate.of(1999, 1, 1))
                .build();

        traineeRepository.update(toUpdate);

        Optional<Trainee> founded = traineeRepository.findById(toUpdate.getId());

        assertTrue(founded.isPresent());
        assertEquals("Updated Street", founded.get().getAddress());
        assertEquals(LocalDate.of(1999, 1, 1), founded.get().getDateOfBirth());
        assertEquals(toUpdate.getUser().getFirstName(), founded.get().getUser().getFirstName());
    }

    @Test
    void shouldDeleteEntityById() {
        Trainee toDelete = constructTrainee();

        Long id = (traineeRepository.save(toDelete)).getId();
        traineeRepository.deleteById(id);

        Optional<Trainee> founded = traineeRepository.findById(id);

        assertTrue(founded.isEmpty());
        assertThrows(EntityNotFoundException.class, ()->trainerRepository.deleteById(id));
    }

    @Test
    void shouldDeleteEntityByUsername() {
        Trainee toDelete = constructTrainee();
        Trainee saved = traineeRepository.save(toDelete);

        traineeRepository.deleteByUsername(toDelete.getUser().getUsername());

        Optional<Trainee> founded = traineeRepository.findById(saved.getId());

        assertTrue(founded.isEmpty());
        assertThrows(EntityNotFoundException.class, () -> traineeRepository.deleteByUsername(toDelete.getUser().getUsername()));
    }

    private static Trainee constructTrainee() {
        int count = counter.incrementAndGet();

        User user = User.builder()
                .firstName("Trainee" + count)
                .lastName("Test")
                .username("trainee" + count)
                .password("Pwd123!@#")
                .isActive(true)
                .build();

        return Trainee.builder()
                .dateOfBirth(LocalDate.of(1990 + count, 1, 13))
                .address("Test address " + count)
                .user(user)
                .build();
    }
}
