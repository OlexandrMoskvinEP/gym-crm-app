package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.dataset.DataSet;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

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

        repository.update(updatedTraining);

        List<Training> persistedTrainings = repository.findAll();

        assertFalse(persistedTrainings.contains(oldTraining));
        assertTrue(persistedTrainings.contains(updatedTraining));
    }

    @ParameterizedTest
    @CsvSource({
            "john.smith,2",
            "olga.ivanova,2",
            "irina.petrova,1"
    })
    void testFindByTraineeCriteria_WithUsername(String username, int expectedCount) {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .build();

        List<Training> result = repository.findByTraineeCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "john.smith,2025-06-01,2025-06-30,2",
            "john.smith,2025-06-01,2025-06-01,1",
            "john.smith,2025-06-11,2025-06-30,0"
    })
    void testFindByTraineeCriteria_WithDateRange(String username, String fromDate, String toDate, int expectedCount) {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .fromDate(LocalDate.parse(fromDate))
                .toDate(LocalDate.parse(toDate))
                .build();

        List<Training> result = repository.findByTraineeCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "john.smith,Boris Krasnov,1",
            "john.smith,Unknown Trainer,0"
    })
    void testFindByTraineeCriteria_WithTrainerName(String username, String trainerName, int expectedCount) {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .trainerFullName(trainerName)
                .build();

        List<Training> result = repository.findByTraineeCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "john.smith,Cardio,1",
            "john.smith,Yoga,0"
    })
    void testFindByTraineeCriteria_WithTrainingType(String username, String trainingTypeName, int expectedCount) {
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .trainingTypeName(trainingTypeName)
                .build();

        List<Training> result = repository.findByTraineeCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "boris.krasnov,2",
            "mykyta.solntcev,1",
            "arnold.schwarzenegger,2"
    })
    void testGetTrainerTrainings_ByUsername(String username, int expectedCount) {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .build();

        List<Training> result = repository.findByTrainerCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "boris.krasnov,2025-06-01,2025-06-30,2",
            "boris.krasnov,2025-06-01,2025-06-01,1",
            "boris.krasnov,2025-06-11,2025-06-30,0"
    })
    void testGetTrainerTrainings_ByDateRange(String username, String from, String to, int expectedCount) {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .fromDate(LocalDate.parse(from))
                .toDate(LocalDate.parse(to))
                .build();

        List<Training> result = repository.findByTrainerCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @ParameterizedTest
    @CsvSource({
            "boris.krasnov,John Smith,1",
            "boris.krasnov,Unknown Trainee,0"
    })
    void testGetTrainerTrainings_ByTraineeFullName(String username, String traineeFullName, int expectedCount) {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .traineeFullName(traineeFullName)
                .build();

        List<Training> result = repository.findByTrainerCriteria(filter);

        assertEquals(expectedCount, result.size());
    }

    @Test
    void testReturnedTrainingDtoFields() {
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username("boris.krasnov")
                .build();

        List<Training> result = repository.findByTrainerCriteria(filter);

        assertFalse(result.isEmpty());
        Training dto = result.iterator().next();
        assertEquals("Morning Cardio", dto.getTrainingName());
        assertEquals("john.smith", dto.getTrainee().getUser().getUsername());
        assertEquals("Cardio", dto.getTrainingType().getTrainingTypeName());
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