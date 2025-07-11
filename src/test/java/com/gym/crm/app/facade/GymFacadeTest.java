package com.gym.crm.app.facade;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

}