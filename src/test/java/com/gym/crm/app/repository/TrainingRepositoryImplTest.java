package com.gym.crm.app.repository;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataSet(value = "datasets/trainings.xml", cleanBefore = true, cleanAfter = true)
public class TrainingRepositoryImplTest extends AbstractRepositoryTest<TrainingRepository> {

    @Test
    void shouldReturnAllTrainings() {
        List<Training> trainings = repository.findAll();

        assertEquals(5, trainings.size());

        Training training = trainings.get(0);
        assertEquals("Morning Cardio", training.getTrainingName());
        assertEquals(LocalDate.of(2025, 6, 1), training.getTrainingDate());
        assertEquals(1, training.getTrainer().getId());
        assertEquals(1, training.getTrainee().getId());
        assertEquals("Cardio", training.getTrainingType().getTrainingTypeName());
    }

    @Test
    void shouldSaveNewTraining() {
        Training newTraining = Training.builder()
                .trainingName("Evening Power")
                .trainingDate(LocalDate.of(2025, 6, 15))
                .trainingDuration(BigDecimal.valueOf(1.5))
                .trainer(Trainer.builder().id(1L).build())
                .trainee(Trainee.builder().id(2L).build())
                .trainingType(TrainingType.builder().id(2L).trainingTypeName("Cardio").build())
                .build();

        Training saved = repository.save(newTraining);

        assertNotNull(saved.getId());
        assertEquals("Evening Power", saved.getTrainingName());
    }

    @Test
    void shouldUpdateTrainingName() {
        Training oldTraining = constructTrainingFromDataset();
        Training updatedTraining = constructUpdatedTraining();

        repository.save(updatedTraining);

        List<Training> persistedTrainings = repository.findAll();
        assertFalse(persistedTrainings.contains(oldTraining));
        assertTrue(persistedTrainings.contains(updatedTraining));
    }

    private Training constructTrainingFromDataset() {
        return Training.builder()
                .id(1L)
                .trainingName("Updated Cardio")
                .trainingDate(LocalDate.of(2025, 6, 1))
                .trainingDuration(BigDecimal.ONE)
                .trainer(Trainer.builder().id(1L).build())
                .trainee(Trainee.builder().id(1L).build())
                .trainingType(TrainingType.builder().id(2L).trainingTypeName("Cardio").build())
                .build();
    }

    private Training constructUpdatedTraining() {
        return Training.builder()
                .id(1L)
                .trainingName("Updated Cardio")
                .trainingDate(LocalDate.of(2025, 8, 14))
                .trainingDuration(BigDecimal.valueOf(2))
                .trainer(Trainer.builder().id(1L).build())
                .trainee(Trainee.builder().id(1L).build())
                .trainingType(TrainingType.builder().id(2L).trainingTypeName("Cardio").build())
                .build();
    }
}