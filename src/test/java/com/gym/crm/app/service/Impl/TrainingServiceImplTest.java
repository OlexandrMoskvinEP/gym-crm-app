package com.gym.crm.app.service.Impl;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    private final ModelMapper modelMapper = new ModelMapper();
    private static final TestData data = new TestData();
    private static final List<Training> trainings = data.getTrainings();

    @Mock
    private TrainingRepository repository;
    @InjectMocks
    private TrainingServiceImpl trainingService;
    @Captor
    private ArgumentCaptor<Training> trainingCaptor;

    @BeforeEach
    void setUp() {
        trainingService = new TrainingServiceImpl();
        trainingService.setTrainingRepository(repository);
        trainingService.setModelMapper(modelMapper);
    }

    @Test
    void shouldReturnAllTrainings() {
        List<TrainingDto> expected = trainings.stream().map(training -> modelMapper.map(training, TrainingDto.class)).toList();

        when(repository.findAll()).thenReturn(trainings);

        List<TrainingDto> actual = trainingService.getAllTrainings();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {321, 204, 205, 206, 207})
    void getTrainingByTrainerId(int trainerId) {
        Training training = trainings.stream().filter(training1 -> training1.getTrainerId() == trainerId).findFirst().get();
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);

        when(repository.findByTrainerId(trainerId)).thenReturn(List.of(training));

        TrainingDto actual = trainingService.getTrainingByTrainerId(trainerId).iterator().next();

        assertEquals(expected.getTrainerId(), actual.getTrainerId());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(ints = {654, 121, 122, 123, 124})
    void getTrainingByTraineeId(int traineeId) {
        Training training = trainings.stream().filter(training1 -> training1.getTraineeId() == traineeId).findFirst().get();
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);

        when(repository.findByTrainerId(traineeId)).thenReturn(List.of(training));

        TrainingDto actual = trainingService.getTrainingByTrainerId(traineeId).iterator().next();

        assertEquals(expected.getTrainerId(), actual.getTrainerId());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getDates")
    void getTrainingByDate(LocalDate date) {
        Training training = trainings.stream().filter(training1 -> training1.getTrainingDate().equals(date)).findFirst().get();
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);

        when(repository.findByDate(date)).thenReturn(List.of(training));

        TrainingDto actual = trainingService.getTrainingByDate(date).iterator().next();

        assertEquals(expected.getTrainerId(), actual.getTrainerId());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainingId")
    void getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto dto) {
        Training training1 = trainings.stream()
                .filter(training -> training.getTrainerId()==dto.getTrainerId())
                .filter(training -> training.getTraineeId()==dto.getTraineeId())
                .filter(training -> training.getTrainingDate().equals(dto.getTrainingDate()))
                .findFirst().get();

        when(repository.findByTrainerAndTraineeAndDate(dto.getTrainerId(), dto.getTraineeId(), dto.getTrainingDate()))
                .thenReturn(Optional.of(training1));

        Optional<TrainingDto>expected = Optional.of(modelMapper.map(training1, TrainingDto.class));
        Optional<TrainingDto>actual = trainingService.getTrainingByTrainerAndTraineeAndDate(dto);

        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainings")
    void shouldAddTraining(Training training) {
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);

        int trainerId = training.getTrainerId();
        int traineeId = training.getTraineeId();
        LocalDate trainingDate = training.getTrainingDate();

        when(repository.saveTraining(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findByTrainerAndTraineeAndDate(trainerId, traineeId, trainingDate))
                .thenReturn(Optional.of(modelMapper.map(expected, Training.class)));

        TrainingDto actual = trainingService.addTraining(modelMapper.map(training, TrainingDto.class));

        verify(repository, atLeastOnce()).saveTraining(trainingCaptor.capture());
        TrainingDto savedTraining = modelMapper.map(trainingCaptor.getValue(), TrainingDto.class);

        assertEquals(expected, savedTraining);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainings")
    void updateTraining(Training training) {
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
        expected.setTrainingName("fakeTrainingName");
        expected.setTrainingType(new TrainingType("fakeTrainingType"));
        expected.setTrainingDuration(240);

        int trainerId = training.getTrainerId();
        int traineeId = training.getTraineeId();
        LocalDate trainingDate = training.getTrainingDate();

        when(repository.findByTrainerAndTraineeAndDate(trainerId, traineeId, trainingDate))
                .thenReturn(Optional.of(modelMapper.map(training, Training.class)));
        when(repository.saveTraining(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainingDto actual = trainingService.updateTraining(modelMapper.map(expected, TrainingDto.class));

        verify(repository, atLeastOnce()).saveTraining(trainingCaptor.capture());
        TrainingDto savedTraining = modelMapper.map(trainingCaptor.getValue(), TrainingDto.class);

        assertEquals(expected, savedTraining);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhetCantDeleteTrainingByTrainerAndTraineeAndDate() {
        when(repository.findByTrainerAndTraineeAndDate(1, 2, LocalDate.EPOCH)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTrainingByTrainerAndTraineeAndDate(
                new TrainingIdentityDto(1, 2, LocalDate.EPOCH)));
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