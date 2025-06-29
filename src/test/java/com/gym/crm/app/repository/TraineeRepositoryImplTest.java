package com.gym.crm.app.repository;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.model.Trainee;
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

import java.time.LocalDate;
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
class TraineeRepositoryImplTest {
    @Mock
    private CommonStorage commonStorage;
    private static final TestData data = new TestData();
    private com.gym.crm.app.repository.impl.TraineeRepositoryImpl repository;
    private final Map<String, Trainee> traineeMap = new HashMap<>(data.getTRAINEE_STORAGE());

    @BeforeEach
    void setUp() {
        when(commonStorage.getTraineeStorage()).thenReturn(traineeMap);

        repository = new com.gym.crm.app.repository.impl.TraineeRepositoryImpl();
        repository.setTraineeStorage(commonStorage);
    }

    @Test
    void shouldReturnAllTrainees() {
        List<Trainee> expected = new ArrayList<>(traineeMap.values());

        List<Trainee> actual = repository.findAll();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void shouldSaveAndReturnSavedTraineeEntity() {
        Trainee trainee = Trainee.builder()
                .firstName("John")
                .lastName("Dou")
                .username("John.Dou")
                .isActive(true)
                .password("iwibfwiyeyb")
                .address("nowhereCity")
                .dateOfBirth(LocalDate.of(1987, 2, 12))
                .userId(1)
                .build();

        Trainee actual = repository.saveTrainee(trainee);
        Trainee savedToStorage = traineeMap.get(trainee.getUsername());

        assertNotNull(actual);
        assertNotNull(savedToStorage);
        assertEquals(trainee, actual);
        assertEquals(trainee, savedToStorage);
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldSaveTraineeEntity(Trainee trainee) {
        assertThrows(DuplicateUsernameException.class, () -> repository.saveTrainee(trainee));
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldFindByUsername(Trainee trainee) {
        String username = trainee.getUsername();

        Trainee actual = repository.findByUsername(username).get();

        assertNotNull(actual);
        assertEquals(trainee, actual);
    }

    @Test
    void shouldDeleteTraineeByUserName() {
        Trainee trainee = Trainee.builder()
                .firstName("John")
                .lastName("Dou")
                .username("John.Dou")
                .isActive(true)
                .password("iwibfwiyeyb")
                .address("nowhereCity")
                .dateOfBirth(LocalDate.of(1987, 2, 12))
                .userId(7896)
                .build();

        repository.saveTrainee(trainee);

        assertDoesNotThrow(() -> repository.deleteByUserName("John.Dou"));
        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("John.Dou"));
    }

    public static Stream<Trainee> getTrainees() {
        return data.getTrainees().stream();
    }
}