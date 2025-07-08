package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.RepositoryIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TrainingRepositoryImplTest extends RepositoryIntegrationTest {
    private static final AtomicInteger counter = new AtomicInteger(300);

    private final List<Training> allTrainings = data.getTestTrainings();

    @Test
    void findAll() {
        List<Training> actual = trainingRepository.findAll();

        assertFalse(actual.isEmpty());
        assertTrue(actual.containsAll(allTrainings));
    }

    @ParameterizedTest
    @ValueSource(longs = {1, 2, 3})
    void shouldFindTrainingById(Long id) {
        Optional<Training> expected = allTrainings.stream().filter(training -> training.getId().equals(id)).findFirst();
        Optional<Training> actual = trainingRepository.findById(id);

        assertTrue(actual.isPresent());

        assertEquals(expected.get().getTrainingDate(), actual.get().getTrainingDate());
        assertEquals(expected.get().getTrainingName(), actual.get().getTrainingName());
        assertEquals(expected.get().getTrainingType(), actual.get().getTrainingType());
        assertEquals(expected.get().getTrainingDuration(), actual.get().getTrainingDuration());
    }

    @Test
    void shouldSaveAndUpdate() {
        Training toUpdate = constructTraining();
        Training saved = trainingRepository.save(toUpdate);

        TrainingType newType = trainingTypeRepository.findById(2L).orElseThrow();
        Trainer newTrainer = Trainer.builder().id(2L).build();
        Trainee newTrainee = Trainee.builder().id(2L).build();

        toUpdate = toUpdate.toBuilder()
                .id(saved.getId())
                .trainingType(newType)
                .trainer(newTrainer)
                .trainee(newTrainee)
                .build();

        trainingRepository.update(toUpdate);

        Optional<Training> founded = trainingRepository.findById(toUpdate.getId());

        assertTrue(founded.isPresent());

        assertEquals(toUpdate.getTrainingDate(), founded.get().getTrainingDate());
        assertEquals(toUpdate.getTrainingName(), founded.get().getTrainingName());
        assertEquals(toUpdate.getTrainingType(), founded.get().getTrainingType());
        assertEquals(toUpdate.getTrainingDuration(), founded.get().getTrainingDuration());
    }

    @Test
    void shouldDeleteById() {
        Training toDelete = constructTraining();
        Training saved = trainingRepository.save(toDelete);

        trainingRepository.deleteById(toDelete.getId());

        Optional<Training> founded = trainingRepository.findById(saved.getId());

        assertTrue(founded.isEmpty());
        assertThrows(EntityNotFoundException.class, () -> trainingRepository.deleteById(toDelete.getId()));
    }

    private Training constructTraining() {
        int count = counter.incrementAndGet();

        TrainingType type = trainingTypeRepository.findById(1L).orElseThrow();
        Trainer trainer = Trainer.builder().id(1L).build();
        Trainee trainee = Trainee.builder().id(1L).build();

        return Training.builder()
                .trainingName("Test training" + count)
                .trainingDate(LocalDate.of(2025, 7, 1))
                .trainingDuration(BigDecimal.ONE)
                .trainer(trainer)
                .trainee(trainee)
                .trainingType(type)
                .build();
    }
}