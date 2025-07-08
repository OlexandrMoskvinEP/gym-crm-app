package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AllRepositorySmokeTest extends RepositoryIntegrationTest {

    @Test
    void findUserByUsername() {
        Optional<User> userOpt = userRepository.findByUsername("john.smith");

        assertTrue(userOpt.isPresent());
        assertEquals("John", userOpt.get().getFirstName());
    }

    @Test
    void allTrainersLoaded() {
        List<Trainer> trainers = trainerRepository.findAll();
        assertEquals(3, trainers.size());
    }

    @Test
    void allTraineesLoaded() {
        List<Trainee> trainees = traineeRepository.findAll();
        assertEquals(3, trainees.size());
    }

    @Test
    void allTrainingTypesLoaded() {
        List<TrainingType> types = trainingTypeRepository.findAll();
        assertEquals(6, types.size());
    }

    @Test
    void allTrainingsLoaded() {
        List<Training> trainings = trainingRepository.findAll();
        assertEquals(5, trainings.size());
    }
}
