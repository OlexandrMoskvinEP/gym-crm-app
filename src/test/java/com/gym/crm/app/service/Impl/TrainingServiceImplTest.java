package com.gym.crm.app.service.Impl;

import com.gym.crm.app.data.TestData;
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

import java.math.BigDecimal;
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
    private static final TestData data = new TestData();
    private static final List<Training> trainings = data.getTrainings();

    private final ModelMapper modelMapper = new ModelMapper();

    @Captor
    private ArgumentCaptor<Training> trainingCaptor;
    @Mock
    private TrainingRepository repository;
    @InjectMocks
    private TrainingServiceImpl trainingService;


    @BeforeEach
    void setUp() {
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

//    @ParameterizedTest
//    @ValueSource(longs = {321L, 204L, 205L, 206L, 207L})
//    void getTrainingByTrainerId(Long trainerId) {
//        Training training = constructTrainingByTrainerId(trainerId);
//        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
//
//        when(repository.findByTrainerId(trainerId)).thenReturn(List.of(training));
//
//        TrainingDto actual = trainingService.getTrainingByTrainerId(trainerId).iterator().next();
//
//        assertEquals(expected.getTrainerId(), actual.getTrainerId());
//        assertEquals(expected, actual);
//    }

//    @ParameterizedTest
//    @ValueSource(longs = {654, 121, 122, 123, 124})
//    void getTrainingByTraineeId(Long traineeId) {
//        Training training = constructTrainingByTraineeId(traineeId);
//        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
//
//        when(repository.findByTrainerId(traineeId)).thenReturn(List.of(training));
//
//        TrainingDto actual = trainingService.getTrainingByTrainerId(traineeId).iterator().next();
//
//        assertEquals(expected.getTrainerId(), actual.getTrainerId());
//        assertEquals(expected, actual);
//    }

//    @ParameterizedTest
//    @MethodSource("getDates")
//    void getTrainingByDate(LocalDate date) {
//        Training training = constructTrainingByDate(date);
//        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
//
//        when(repository.findByDate(date)).thenReturn(List.of(training));
//
//        TrainingDto actual = trainingService.getTrainingByDate(date).iterator().next();
//
//        assertEquals(expected.getTrainerId(), actual.getTrainerId());
//        assertEquals(expected, actual);
//    }


//    @ParameterizedTest
//    @MethodSource("getTrainingId")
//    void getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto dto) {
//        Training training1 = getTraining(dto);
//
//        when(repository.findByTrainerAndTraineeAndDate(dto.getTrainerId(), dto.getTraineeId(), dto.getTrainingDate()))
//                .thenReturn(Optional.of(training1));
//
//        Optional<TrainingDto> expected = Optional.of(modelMapper.map(training1, TrainingDto.class));
//
//        Optional<TrainingDto> actual = trainingService.getTrainingByTrainerAndTraineeAndDate(dto);
//
//        assertNotNull(actual);
//        assertEquals(expected, actual);
//    }

    @ParameterizedTest
    @MethodSource("getTrainings")
    void shouldAddTraining(Training training) {
        TrainingDto expected = getTrainingDtoFromEntity(training);

        when(repository.save(any(Training.class))).thenReturn(training);

        TrainingDto actual = trainingService.addTraining(expected);

        verify(repository, atLeastOnce()).save(trainingCaptor.capture());

        TrainingDto savedTraining = getTrainingDtoFromEntity(trainingCaptor.getValue());

        assertEquals(expected, savedTraining);
        assertEquals(expected, actual);
    }

//    @ParameterizedTest
//    @MethodSource("getTrainings")
//    void shouldUpdateTraining(Training training) {
//        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
//
//        expected.setTrainingName("fakeTrainingName");
//        expected.setTrainingType(new TrainingType(1l, "fakeTrainingType"));
//        expected.setTrainingDuration(BigDecimal.valueOf(240));
//
//        long trainerId = training.getTrainer().getId();
//        long traineeId = training.getTrainee().getId();
//        LocalDate trainingDate = training.getTrainingDate();
//
//        when(repository.findByTrainerAndTraineeAndDate(trainerId, traineeId, trainingDate))
//                .thenReturn(Optional.of(modelMapper.map(training, Training.class)));
//        when(repository.save(any(Training.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        TrainingDto actual = trainingService.updateTraining(modelMapper.map(expected, TrainingDto.class));
//
//        verify(repository, atLeastOnce()).save(trainingCaptor.capture());
//
//        TrainingDto savedTraining = modelMapper.map(trainingCaptor.getValue(), TrainingDto.class);
//
//        assertEquals(expected, savedTraining);
//        assertEquals(expected, actual);
//    }

//    @Test
//    void shouldThrowExceptionWhetCantDeleteTrainingByTrainerAndTraineeAndDate() {
//        when(repository.findByTrainerAndTraineeAndDate(1L, 2L, LocalDate.EPOCH)).thenReturn(Optional.empty());
//
//        assertThrows(EntityNotFoundException.class, () -> trainingService.deleteTrainingByTrainerAndTraineeAndDate(
//                new TrainingIdentityDto(1L, 2L, LocalDate.EPOCH)));
//    }

    private static Stream<Training> getTrainings() {
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

    private static Stream<TrainingIdentityDto> getTrainingId() {
        return data.getIdentities().stream();
    }

    private Training constructTrainingByDate(LocalDate date) {
        return trainings.stream()
                .filter(training -> training.getTrainingDate().equals(date))
                .findFirst().get();
    }

    private Training constructTrainingByTraineeId(Long traineeId) {
        return trainings.stream()
                .filter(training -> training.getTrainee().getId().equals(traineeId))
                .findFirst().get();
    }

    private Training constructTrainingByTrainerId(Long trainerId) {
        return trainings.stream()
                .filter(training -> training.getTrainer().getId().equals(trainerId))
                .findFirst().get();
    }

    private static Training getTraining(TrainingIdentityDto dto) {
        return trainings.stream()
                .filter(training -> training.getTrainer().getId().equals(dto.getTrainerId()))
                .filter(training -> training.getTrainee().getId().equals(dto.getTraineeId()))
                .filter(training -> training.getTrainingDate().equals(dto.getTrainingDate()))
                .findFirst().get();
    }

    private TrainingDto getTrainingDtoFromEntity(Training training) {
        return new TrainingDto(training.getTrainer().getId(),
                training.getTrainee().getId(),
                training.getTrainingName(),
                training.getTrainingType(),
                training.getTrainingDate(),
                training.getTrainingDuration());
    }
}