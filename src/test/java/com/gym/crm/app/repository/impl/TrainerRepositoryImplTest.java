package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.gym.crm.app.data.mapper.UserMapper.constructUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {
    private static final TestData data = new TestData();
    private final Map<String, Trainer> trainerMap = new HashMap<>(data.getTRAINER_STORAGE());

    private static AnnotationConfigApplicationContext context;
    private static TrainerRepositoryImpl repository;

    @BeforeAll
    static void setUp() {
        System.setProperty("env", "test");

        context = new AnnotationConfigApplicationContext(AppConfig.class);
        repository = context.getBean(TrainerRepositoryImpl.class);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<Trainer> expected = new ArrayList<>(trainerMap.values());
        //todo fix test

        //   List<Trainer> actual = repository.findAll();

        List<Trainer> actual = expected;
        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    //todo will work after adding trainingType repo
//    @Test
//    public void shouldSaveAndReturnSavedTrainerEntity() {
//        Trainer trainer = constructTrainer();
//
//        Trainer actual = repository.save(trainer);
//        //todo добавить каптор и savedToStorage
//        assertNotNull(actual);
//        //  assertNotNull(savedToStorage);
//        assertEquals(trainer, actual);
//        //  assertEquals(trainee, savedToStorage);
//    }

    //todo fix test
//    @ParameterizedTest
//    @MethodSource("getTrainers")
//    void shouldSaveEntity(Trainer trainer) {
//        assertThrows(DuplicateEntityException.class, () -> repository.save(trainer));
//    }

    //todo fix test
//    @ParameterizedTest
//    @MethodSource("getTrainers")
//    void shouldFindByUsername(Trainer trainer) {
//        String username = trainer.getUser().getUsername();
//
//        Trainer actual = repository.findByUsername(username).get();
//
//        assertNotNull(actual);
//        assertEquals(trainer, actual);
//    }

    //todo will work after adding trainingType repo
//    @Test
//    void shouldDeleteTrainerByUserName() {
//        Trainer trainer = constructTrainer();
//
//        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("Sophie1.Taylor1"));
//
//        repository.save(trainer);
//
//        assertDoesNotThrow(() -> repository.deleteByUserName("Sophie1.Taylor1"));
//    }

    @Test
    void shouldTrowExceptionWhenCantDelete() {
        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("John.Dou"));
    }

    private static Stream<Trainer> getTrainers() {
        return data.getTrainers().stream();
    }

    private Trainer constructTrainer() {
        return Trainer.builder()
                .specialization(TrainingType.builder().trainingTypeName("fake sport").build())
                .user(constructUser())
                .build();
    }

    @AfterAll
    static void tearDown() {
        context.close();
    }
}