package com.gym.crm.app.repository;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.User;
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
        trainee = trainee.toBuilder().id(1L).build();

        Trainee actual = repository.saveTrainee(trainee);
        Trainee savedToStorage = traineeMap.get(trainee.getUser().getUsername());

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
        String username = trainee.getUser().getUsername();

        Trainee actual = repository.findByUsername(username).get();

        assertNotNull(actual);
        assertEquals(trainee, actual);
    }

    @Test
    void shouldDeleteTraineeByUserName() {
        Trainee trainee = constructTrainee();

        assertThrows(EntityNotFoundException.class, () -> repository.deleteByUserName("John.Dou"));

        repository.saveTrainee(trainee);

        assertDoesNotThrow(() -> repository.deleteByUserName("Alice.Moro"));
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
                .dateOfBirth(LocalDate.of(1990, 3, 12))
                .address("Main Street")
                .user(constructUser())
                .build();
    }

    private static User constructUser() {
        return User.builder()
                .firstName("Alice")
                .lastName("Moro")
                .username("Alice.Moro")
                .password("Abc123!@#")
                .isActive(true)
                .build();
    }
}