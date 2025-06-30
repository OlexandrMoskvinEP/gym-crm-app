package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.facade.GymFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = AppConfig.class)
class MainTest {
    @Autowired
    private GymFacade gymFacade;

    private List<TrainerDto> trainers;
    private List<TraineeDto> trainees;
    private List<TrainingDto> trainings;

    @BeforeEach
    void setUp() {
        trainers = gymFacade.getAllTrainers();
        trainees = gymFacade.getAllTrainees();
        trainings = gymFacade.getAllTrainings();
    }

    @Test
    void shouldLoadContext() {
        assertNotNull(gymFacade);
        assertNotNull(trainers);
        assertNotNull(trainees);
        assertNotNull(trainings);
    }
}