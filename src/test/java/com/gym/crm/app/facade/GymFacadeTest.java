package com.gym.crm.app.facade;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {
    private final ModelMapper modelMapper = new ModelMapper();
    private final TestData data = new TestData();

    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;

    @InjectMocks
    private GymFacade gymFacade;

    @BeforeEach
    void setUp() {
        gymFacade = new GymFacade(traineeService, trainerService, trainingService);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<TrainerDto> expected = data.getTrainers().stream()
                .map(trainer -> modelMapper.map(trainer, TrainerDto.class))
                .toList();

        when(trainerService.getAllTrainers()).thenReturn(expected);

        List<TrainerDto> actual = gymFacade.getAllTrainers();

        verify(trainerService).getAllTrainers();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        TrainerDto expected = modelMapper.map(data.getTrainers().get(0), TrainerDto.class);

        when(trainerService.getTrainerByUsername("username")).thenReturn(expected);

        TrainerDto actual = gymFacade.getTrainerByUsername("username");

        verify(trainerService).getTrainerByUsername("username");
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddTrainer() {
        TrainerDto trainerDto = modelMapper.map(data.getTrainers().get(0), TrainerDto.class);

        when(trainerService.addTrainer(trainerDto)).thenReturn(trainerDto);

        TrainerDto actual = gymFacade.addTrainer(trainerDto);

        verify(trainerService).addTrainer(trainerDto);
        assertEquals(trainerDto, actual);
    }

    @Test
    void shouldUpdateTrainerByUsername() {
        TrainerDto trainerDto = modelMapper.map(data.getTrainers().get(0), TrainerDto.class);

        when(trainerService.updateTrainerByUsername("username", trainerDto)).thenReturn(trainerDto);

        TrainerDto actual = gymFacade.updateTrainerByUsername("username", trainerDto);

        verify(trainerService).updateTrainerByUsername("username", trainerDto);
        assertEquals(trainerDto, actual);
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        doNothing().when(trainerService).deleteTrainerByUsername("username");

        gymFacade.deleteTrainerByUsername("username");

        verify(trainerService).deleteTrainerByUsername("username");
    }

    @Test
    void shouldReturnAllTrainees() {
        List<TraineeDto> expected = data.getTrainees().stream()
                .map(trainee -> modelMapper.map(trainee, TraineeDto.class))
                .toList();

        when(traineeService.getAllTrainees()).thenReturn(expected);

        List<TraineeDto> actual = gymFacade.getAllTrainees();

        verify(traineeService).getAllTrainees();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTraineeByUsername() {
        TraineeDto expected = modelMapper.map(data.getTrainees().get(0), TraineeDto.class);

        when(traineeService.getTraineeByUsername("username")).thenReturn(expected);

        TraineeDto actual = gymFacade.getTraineeByUsername("username");

        verify(traineeService).getTraineeByUsername("username");
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddTrainee() {
        TraineeDto traineeDto = modelMapper.map(data.getTrainees().get(0), TraineeDto.class);

        when(traineeService.addTrainee(traineeDto)).thenReturn(traineeDto);

        TraineeDto actual = gymFacade.addTrainee(traineeDto);

        verify(traineeService).addTrainee(traineeDto);
        assertEquals(traineeDto, actual);
    }

    @Test
    void shouldUpdateTraineeByUsername() {
        TraineeDto traineeDto = modelMapper.map(data.getTrainees().get(0), TraineeDto.class);

        when(traineeService.updateTraineeByUsername("username", traineeDto)).thenReturn(traineeDto);

        TraineeDto actual = gymFacade.updateTraineeByUsername("username", traineeDto);

        verify(traineeService).updateTraineeByUsername("username", traineeDto);
        assertEquals(traineeDto, actual);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        doNothing().when(traineeService).deleteTraineeByUsername("username");

        gymFacade.deleteTraineeByUsername("username");

        verify(traineeService).deleteTraineeByUsername("username");
    }

    @Test
    void shouldReturnAllTrainings() {
        List<TrainingDto> expected = data.getTrainings().stream()
                .map(training -> modelMapper.map(training, TrainingDto.class))
                .toList();

        when(trainingService.getAllTrainings()).thenReturn(expected);

        List<TrainingDto> actual = gymFacade.getAllTrainings();

        verify(trainingService).getAllTrainings();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrainingByTrainerId() {
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.getTrainingByTrainerId(1)).thenReturn(List.of(trainingDto));

        List<TrainingDto> actual = gymFacade.getTrainingByTrainerId(1);

        verify(trainingService).getTrainingByTrainerId(1);
        assertEquals(List.of(trainingDto), actual);
    }

    @Test
    void shouldReturnTrainingByTraineeId() {
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.getTrainingByTraineeId(1)).thenReturn(List.of(trainingDto));

        List<TrainingDto> actual = gymFacade.getTrainingByTraineeId(1);

        verify(trainingService).getTrainingByTraineeId(1);
        assertEquals(List.of(trainingDto), actual);
    }

    @Test
    void shouldReturnTrainingByDate() {
        LocalDate date = LocalDate.now();
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.getTrainingByDate(date)).thenReturn(List.of(trainingDto));

        List<TrainingDto> actual = gymFacade.getTrainingByDate(date);

        verify(trainingService).getTrainingByDate(date);
        assertEquals(List.of(trainingDto), actual);
    }

    @Test
    void shouldReturnTrainingByTrainerAndTraineeAndDate() {
        TrainingIdentityDto dto = new TrainingIdentityDto(1, 2, LocalDate.now());
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.getTrainingByTrainerAndTraineeAndDate(dto)).thenReturn(Optional.of(trainingDto));

        Optional<TrainingDto> actual = gymFacade.getTrainingByTrainerAndTraineeAndDate(dto);

        verify(trainingService).getTrainingByTrainerAndTraineeAndDate(dto);

        assertNotNull(actual);
        assertEquals(Optional.of(trainingDto), actual);
    }

    @Test
    void shouldAddTraining() {
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.addTraining(trainingDto)).thenReturn(trainingDto);

        TrainingDto actual = gymFacade.addTraining(trainingDto);

        verify(trainingService).addTraining(trainingDto);

        assertEquals(trainingDto, actual);
    }

    @Test
    void shouldUpdateTraining() {
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);

        when(trainingService.updateTraining(trainingDto)).thenReturn(trainingDto);

        TrainingDto actual = gymFacade.updateTraining(trainingDto);

        verify(trainingService).updateTraining(trainingDto);

        assertEquals(trainingDto, actual);
    }

    @Test
    void shouldDeleteTrainingByTrainerAndTraineeAndDate() {
        TrainingIdentityDto dto = new TrainingIdentityDto(1, 2, LocalDate.now());

        doNothing().when(trainingService).deleteTrainingByTrainerAndTraineeAndDate(dto);

        gymFacade.deleteTrainingByTrainerAndTraineeAndDate(dto);

        verify(trainingService).deleteTrainingByTrainerAndTraineeAndDate(dto);
    }
}