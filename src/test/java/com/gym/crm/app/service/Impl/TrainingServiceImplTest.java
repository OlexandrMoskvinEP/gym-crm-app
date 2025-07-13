package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.TrainingTypeRepository;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingServiceImplTest {
    public static final long TRAINEE_ID = 1L;
    public static final long TRAINER_ID = 2L;
    public static final long TRAINING_TYPE_ID = 3L;
    public static final String TRAINING_TYPE_NAME = "Cardio";
    public static final String TRAINING_NAME = "Morning Workout";
    public static final LocalDate TRAINING_DATE = LocalDate.of(2024, 1, 1);
    public static final BigDecimal TRAINING_DURATION = BigDecimal.TEN;

    public static final Trainee TRAINEE = Trainee.builder().id(TRAINEE_ID).build();
    public static final Trainer TRAINER = Trainer.builder().id(TRAINER_ID).build();
    public static final TrainingType TRAINING_TYPE = TrainingType.builder()
            .id(TRAINING_TYPE_ID)
            .trainingTypeName(TRAINING_TYPE_NAME)
            .build();

    private final ModelMapper modelMapper = new ModelMapper();

    @Captor
    private ArgumentCaptor<Training> trainingCaptor;

    @Mock
    private TrainingTypeRepository trainingTypeRepository;
    @Mock
    private TrainingRepository repository;
    @Mock
    private TrainingMapper trainingMapper;
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
        List<TrainingDto> expected = List.of(buildTrainingDto());

        when(repository.findAll()).thenReturn(List.of(buildTraining()));

        List<TrainingDto> actual = trainingService.getAllTrainings();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddTraining() {
        Training training = buildTraining();
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

    @Test
    void shouldUpdateTraining() {
        TrainingDto expected = buildTrainingDto();
        expected.setTrainingName("fakeTrainingName");
        expected.setTrainingType(new TrainingType(1l, "fakeTrainingType"));
        expected.setTrainingDuration(BigDecimal.valueOf(240));

        when(trainingTypeRepository.findByName(any()))
                .thenReturn(extractTrainingType(expected));
        when(repository.findAll()).thenReturn(List.of(buildTraining()));
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

    @Test
    void shouldReturnTrainingsForSearchByTraineeCriteria() {
        Training training = buildTraining();
        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder().build();

        when(trainingMapper.toDto(training)).thenReturn(buildTrainingDto());
        when(repository.findByTraineeCriteria(filter)).thenReturn(List.of(training));

        List<TrainingDto> result = trainingService.getTraineeTrainingsByFilter(filter);

        assertEquals(1, result.size());

        TrainingDto dto = result.get(0);
        assertEquals(2L, dto.getTrainerId());
        assertEquals(1L, dto.getTraineeId());
        assertEquals("Cardio", dto.getTrainingType().getTrainingTypeName());
        assertEquals("Morning Workout", dto.getTrainingName());
        assertEquals(LocalDate.of(2024, 1, 1), dto.getTrainingDate());
        assertEquals(BigDecimal.TEN, dto.getTrainingDuration());

        verify(repository).findByTraineeCriteria(filter);
    }

    @Test
    void shouldReturnTrainingsForSearchByTrainerCriteria() {
        Training training = buildTraining();
        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder().build();

        when(trainingMapper.toDto(training)).thenReturn(buildTrainingDto());
        when(repository.findByTrainerCriteria(filter)).thenReturn(List.of(training));

        List<TrainingDto> result = trainingService.getTrainerTrainingsByFilter(filter);

        assertEquals(1, result.size());

        TrainingDto dto = result.get(0);
        assertEquals(2L, dto.getTrainerId());
        assertEquals(1L, dto.getTraineeId());
        assertEquals("Cardio", dto.getTrainingType().getTrainingTypeName());
        assertEquals("Morning Workout", dto.getTrainingName());
        assertEquals(LocalDate.of(2024, 1, 1), dto.getTrainingDate());
        assertEquals(BigDecimal.TEN, dto.getTrainingDuration());

        verify(repository).findByTrainerCriteria(filter);
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

    private Training buildTraining() {
        return Training.builder()
                .trainee(TRAINEE)
                .trainer(TRAINER)
                .trainingType(TRAINING_TYPE)
                .trainingName(TRAINING_NAME)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .build();
    }

    private TrainingDto buildTrainingDto() {
        return TrainingDto.builder()
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .trainingDate(TRAINING_DATE)
                .trainingDuration(TRAINING_DURATION)
                .trainingName(TRAINING_NAME)
                .trainingType(TRAINING_TYPE)
                .build();
    }
}