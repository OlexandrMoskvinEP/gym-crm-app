package com.gym.crm.app.repository;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.impl.TraineeRepositoryImpl;
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
    private static final TestData data = new TestData();

    private final Map<String, Trainee> traineeMap = new HashMap<>(data.getTRAINEE_STORAGE());

    @Mock
    private CommonStorage commonStorage;

    private TraineeRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(commonStorage.getTraineeStorage()).thenReturn(traineeMap);

        repository = new TraineeRepositoryImpl();
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
        Trainee trainee = constructTrainee();

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
        Trainee trainee = constructTrainee();

        repository.saveTrainee(trainee);

        assertDoesNotThrow(() -> repository.deleteByUserName("John.Dou"));
    }

    @Test
    public void shouldThrowExceptionWhenCantDelete() {
        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("John.Dou"));
    }

    private static Stream<Trainee> getTrainees() {
        return data.getTrainees().stream();
    }

    private Trainee constructTrainee() {
        return Trainee.builder()
                .firstName("John")
                .lastName("Dou")
                .username("John.Dou")
                .isActive(true)
                .password("iwibfwiyeyb")
                .address("nowhereCity")
                .dateOfBirth(LocalDate.of(1987, 2, 12))
                .userId(1)
                .build();
    }
}