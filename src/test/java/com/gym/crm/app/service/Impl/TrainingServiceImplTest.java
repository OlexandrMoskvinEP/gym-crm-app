package com.gym.crm.app.service.Impl;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.TrainingTypeRepository;
import com.gym.crm.app.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
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
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingRepository repository;
    @InjectMocks
    private TrainingServiceImpl trainingService;


    @BeforeEach
    void setUp() {
        trainingService.setModelMapper(modelMapper);
        lenient().when(trainingTypeRepository.findByName(any()))
                .thenReturn(Optional.of(new TrainingType(1L, "Yoga")));
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
    @MethodSource("getTrainings")
    void shouldAddTraining(Training training) {
        TrainingSaveRequest saveRequest = getTrainingDtoFromEntity(training);
        when(trainingTypeRepository.findByName(any()))
                .thenReturn(extractTrainingType(training));
        when(repository.save(any(Training.class))).thenReturn(training);

        TrainingDto actual = trainingService.addTraining(saveRequest);

        verify(repository, atLeastOnce()).save(trainingCaptor.capture());
        TrainingSaveRequest savedTraining = getTrainingDtoFromEntity(trainingCaptor.getValue());
        assertEquals(saveRequest, savedTraining);
        assertEquals(saveRequest.getTrainingName(), actual.getTrainingName());
        assertEquals(saveRequest.getTraineeId(), actual.getTraineeId());
        assertEquals(saveRequest.getTrainerId(), actual.getTrainerId());
        assertEquals(saveRequest.getTrainingDate(), actual.getTrainingDate());
        assertEquals(saveRequest.getTrainingDuration(), actual.getTrainingDuration());
        assertEquals(saveRequest.getTrainingTypeName(), actual.getTrainingType().getTrainingTypeName());
    }

    @ParameterizedTest
    @MethodSource("getTrainings")
    void shouldUpdateTraining(Training training) {
        TrainingDto expected = modelMapper.map(training, TrainingDto.class);
        expected.setTrainingName("fakeTrainingName");
        expected.setTrainingType(new TrainingType(1l, "fakeTrainingType"));
        expected.setTrainingDuration(BigDecimal.valueOf(240));

        when(trainingTypeRepository.findByName(any()))
                .thenReturn(extractTrainingType(expected));
        when(repository.findAll()).thenReturn(trainings);
        when(repository.save(any(Training.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TrainingDto actual = trainingService.updateTraining(modelMapper.map(expected, TrainingSaveRequest.class));

        verify(repository, atLeastOnce()).save(trainingCaptor.capture());
        TrainingDto savedTraining = modelMapper.map(trainingCaptor.getValue(), TrainingDto.class);

        assertEquals("fakeTrainingName", savedTraining.getTrainingName());
        assertEquals("fakeTrainingType", savedTraining.getTrainingType().getTrainingTypeName());
        assertEquals(BigDecimal.valueOf(240), savedTraining.getTrainingDuration());
        assertEquals("fakeTrainingName", actual.getTrainingName());
        assertEquals(BigDecimal.valueOf(240), actual.getTrainingDuration());
    }

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

    private TrainingSaveRequest getTrainingDtoFromEntity(Training training) {
        return TrainingSaveRequest.builder()
                .trainingName(training.getTrainingName())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .traineeId(training.getTrainee().getId())
                .trainerId(training.getTrainer().getId())
                .trainingTypeName(training.getTrainingType().getTrainingTypeName())
                .build();
    }

    private Optional<TrainingType> extractTrainingType(Training training) {
        TrainingType trainingType = new TrainingType(training.getTrainingType().getId(),
                training.getTrainingType().getTrainingTypeName());

        return Optional.of(trainingType);
    }

    private Optional<TrainingType> extractTrainingType(TrainingDto training) {
        TrainingType trainingType = new TrainingType(training.getTrainingType().getId(),
                training.getTrainingType().getTrainingTypeName());

        return Optional.of(trainingType);
    }
}