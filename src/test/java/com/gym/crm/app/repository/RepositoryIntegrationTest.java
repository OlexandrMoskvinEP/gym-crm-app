package com.gym.crm.app.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.config.TestConfig;
import com.gym.crm.app.data.TestData;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DBRider
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@DBUnit(schema = "PUBLIC")
public abstract class RepositoryIntegrationTest {
    private static AnnotationConfigApplicationContext context;

    protected static UserRepository userRepository;
    protected static TrainerRepository trainerRepository;
    protected static TraineeRepository traineeRepository;
    protected static TrainingTypeRepository trainingTypeRepository;
    protected static TrainingRepository trainingRepository;

    protected TestData data = new TestData();

    @BeforeAll
    static void initContext() {
        context = new AnnotationConfigApplicationContext(AppConfig.class);

        userRepository = context.getBean(UserRepository.class);
        trainerRepository = context.getBean(TrainerRepository.class);
        traineeRepository = context.getBean(TraineeRepository.class);
        trainingTypeRepository = context.getBean(TrainingTypeRepository.class);
        trainingRepository = context.getBean(TrainingRepository.class);
    }

    @AfterAll
    static void closeContext() {
        context.close();
    }
}
