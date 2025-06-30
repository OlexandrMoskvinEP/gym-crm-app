package com.gym.crm.app.repository;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.impl.TrainingRepositoryImpl;
import com.gym.crm.app.storage.CommonStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
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
class TrainingRepositoryImplTest {
    @Mock
    private CommonStorage commonStorage;
    private static final TestData data = new TestData();
    private TrainingRepositoryImpl repository;
    private final Map<String, Training> trainingMap = new HashMap<>(data.getTRAINING_STORAGE());

    @BeforeEach
    void setUp() {
        when(commonStorage.getTrainingStorage()).thenReturn(trainingMap);

        repository = new TrainingRepositoryImpl();
        repository.setTrainingStorage(commonStorage);
    }

    @Test
    void shouldReturnAllTraining() {
        List<Training> expected = new ArrayList<>(trainingMap.values());

        List<Training> actual = repository.findAll();

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {321, 204, 205, 206, 207})
    void shouldFindEntityByTrainerId(int trainerId) {
        List<Training> expected = new ArrayList<>(trainingMap.values())
                .stream().filter(training -> training.getTrainerId() == trainerId).toList();

        List<Training> actual = repository.findByTrainerId(trainerId);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {654, 121, 122, 123, 124})
    void shouldFindEntityByTraineeId(int traineeId) {
        List<Training> expected = new ArrayList<>(trainingMap.values())
                .stream().filter(training -> training.getTraineeId() == traineeId).toList();

        List<Training> actual = repository.findByTraineeId(traineeId);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getDates")
    void shouldFindEntityByDate(LocalDate date) {
        List<Training> expected = new ArrayList<>(trainingMap.values())
                .stream().filter(training -> training.getTrainingDate().equals(date)).toList();

        List<Training> actual = repository.findByDate(date);

        assertNotNull(actual);
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainingId")
    void shouldFindEntityByTrainerAndTraineeAndDate(TrainingIdentityDto dto) {
        Training expected = trainingMap.values().stream()
                .filter(training -> training.getTrainerId() == dto.getTrainerId())
                .filter(training -> training.getTraineeId() == dto.getTraineeId())
                .filter(training -> training.getTrainingDate().equals(dto.getTrainingDate()))
                .findFirst().get();

        Training actual = repository.findByTrainerAndTraineeAndDate(dto.getTrainerId(), dto.getTraineeId(), dto.getTrainingDate()).get();

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainings")
    void shouldSaveEntity(Training training) {
        assertThrows(DuplicateUsernameException.class, () -> repository.saveTraining(training));
    }

    @Test
    void deleteByTrainerAndTraineeAndDate() {
        Training training = Training.builder()
                .trainingDate(LocalDate.EPOCH)
                .trainingName("fakeTraining")
                .trainingType(new TrainingType("fakeSport"))
                .trainingDuration(240)
                .traineeId(123)
                .trainerId(4567)
                .build();

        repository.saveTraining(training);

        assertDoesNotThrow(() -> repository
                .deleteByTrainerAndTraineeAndDate(training.getTrainerId(), training.getTraineeId(), training.getTrainingDate()));
        assertThrows(EntityNotFoundException.class, () -> repository
                .deleteByTrainerAndTraineeAndDate(training.getTrainerId(), training.getTraineeId(), training.getTrainingDate()));
    }

    public static Stream<Training> getTrainings() {
        return data.getTrainings().stream();
    }

    static Stream<LocalDate> getDates() {
        return Stream.of(
                LocalDate.of(2025, 6, 28),
                LocalDate.of(2025, 6, 27),
                LocalDate.of(2025, 6, 26),
                LocalDate.of(2025, 6, 25),
                LocalDate.of(2025, 6, 24)
        );
    }

    public static Stream<TrainingIdentityDto> getTrainingId() {
        return data.getIdentities().stream();
    }
}