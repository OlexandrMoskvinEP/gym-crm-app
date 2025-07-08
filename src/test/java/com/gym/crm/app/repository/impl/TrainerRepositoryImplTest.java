package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerRepositoryImplTest extends RepositoryIntegrationTest {
    private static final AtomicInteger counter = new AtomicInteger(200);

    private final List<Trainer> allTrainers = data.getTestTrainers();

    @Test
    void shouldReturnAllTrainers() {
        List<Trainer> actual = trainerRepository.findAll();

        assertFalse(actual.isEmpty());
        assertTrue(actual.containsAll(allTrainers));
    }

    @Test
    void shouldSaveEntity() {
        Trainer toSave = constructTrainer();

        Trainer saved = trainerRepository.save(toSave);
        Trainer found = trainerRepository.findByUsername(toSave.getUser().getUsername()).orElse(null);

        assertNotNull(saved);
        assertNotNull(found);
        assertEquals(toSave.getUser().getUsername(), found.getUser().getUsername());
    }

    @Test
    void shouldSaveAndUpdate() {
        Trainer toUpdate = constructTrainer();
        Trainer saved = trainerRepository.save(toUpdate);
        TrainingType newSpecialisation = trainingTypeRepository.findById(2L).orElseThrow();

        toUpdate = toUpdate.toBuilder()
                .id(saved.getId())
                .user(saved.getUser())
                .specialization(newSpecialisation)
                .build();

        trainerRepository.update(toUpdate);


        Optional<Trainer> founded = trainerRepository.findById(toUpdate.getId());

        assertTrue(founded.isPresent());
        assertEquals(newSpecialisation.getTrainingTypeName(), founded.get().getSpecialization().getTrainingTypeName());
        assertEquals(toUpdate.getUser().getFirstName(), founded.get().getUser().getFirstName());
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void shouldFindTrainerById(Long id) {
        Optional<Trainer> expected = allTrainers.stream().filter(trainer -> trainer.getId().equals(id)).findFirst();
        Optional<Trainer> actual = trainerRepository.findById(id);

        assertTrue(actual.isPresent());
        assertEquals(expected.get().getSpecialization().getTrainingTypeName(), actual.get().getSpecialization().getTrainingTypeName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"boris.krasnov", "mykyta.solntcev", "arnold.schwarzenegger"})
    void shouldFindByUsername(String username) {
        Optional<Trainer> actual = trainerRepository.findByUsername(username);

        assertTrue(actual.isPresent());
        assertEquals(username, actual.get().getUser().getUsername());
    }

    @Test
    void shouldDeleteEntityById() {
        Trainer toDelete = constructTrainer();

        Long id = (trainerRepository.save(toDelete)).getId();
        trainerRepository.deleteById(id);

        Optional<Trainer> founded = trainerRepository.findById(id);

        assertTrue(founded.isEmpty());
        assertThrows(EntityNotFoundException.class, ()->trainerRepository.deleteById(id));
    }

    @Test
    void shouldDeleteEntityByUsername() {
        Trainer toDelete = constructTrainer();
        Trainer saved = trainerRepository.save(toDelete);

        trainerRepository.deleteByUsername(toDelete.getUser().getUsername());

        Optional<Trainer> founded = trainerRepository.findById(saved.getId());

        assertTrue(founded.isEmpty());
        assertThrows(EntityNotFoundException.class, () -> trainerRepository.deleteByUsername(toDelete.getUser().getUsername()));
    }

    private Trainer constructTrainer() {
        int count = counter.incrementAndGet();
        TrainingType type = trainingTypeRepository.findById(1L).orElseThrow();

        User user = User.builder()
                .firstName("Trainer" + count)
                .lastName("Test")
                .username("trainer" + count)
                .password("Pwd123!@#")
                .isActive(true)
                .build();
        return Trainer.builder()
                .specialization(type)
                .user(user)
                .build();
    }
}