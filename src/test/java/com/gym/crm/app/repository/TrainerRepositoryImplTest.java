package com.gym.crm.app.repository;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.storage.CommonStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {
    @Mock
    private CommonStorage commonStorage;
    private static final TestData data = new TestData();
    private com.gym.crm.app.repository.impl.TrainerRepositoryImpl repository;
    private final Map<String, Trainer> trainerMap = new HashMap<>(data.getTRAINER_STORAGE());

    @BeforeEach
    void setUp() {
        when(commonStorage.getTrainerStorage()).thenReturn(trainerMap);

        repository = new com.gym.crm.app.repository.impl.TrainerRepositoryImpl();
        repository.setTrainerStorage(commonStorage);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<Trainer> expected = new ArrayList<>(trainerMap.values());

        List<Trainer> actual = repository.findAll();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldSaveAndReturnSavedTrainerEntity() {
        Trainer trainer = Trainer.builder()
                .firstName("John")
                .lastName("Dou")
                .username("John.Dou")
                .isActive(true)
                .password("iwibfwiyeyb")
                .specialization(new TrainingType("fakeSport"))
                .userId(1)
                .build();

        Trainer actual = repository.saveTrainer(trainer);
        Trainer savedToStorage = trainerMap.get(trainer.getUsername());

        assertNotNull(actual);
        assertNotNull(savedToStorage);
        assertEquals(trainer, actual);
        assertEquals(trainer, savedToStorage);
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldSaveTrainerEntity(Trainer trainer) {
        assertThrows(DuplicateUsernameException.class, () -> repository.saveTrainer(trainer));
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldFindByUsername(Trainer trainer) {
        String username = trainer.getUsername();

        Trainer actual = repository.findByUsername(username).get();

        assertNotNull(actual);
        assertEquals(trainer, actual);
    }

    @Test
    void shouldDeleteTrainerByUserName() {
        Trainer trainer = Trainer.builder()
                .firstName("John")
                .lastName("Dou")
                .username("John.Dou")
                .isActive(true)
                .password("iwibfwiyeyb")
                .specialization(new TrainingType("fakeSport"))
                .userId(7896)
                .build();

        repository.saveTrainer(trainer);

        assertDoesNotThrow(() -> repository.deleteByUserName("John.Dou"));
        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("John.Dou"));
    }

    public static Stream<Trainer> getTrainers() {
        return data.getTrainers().stream();
    }
}