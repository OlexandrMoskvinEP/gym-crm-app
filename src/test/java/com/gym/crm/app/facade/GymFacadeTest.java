package com.gym.crm.app.facade;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.trainee.TraineeResponse;
import com.gym.crm.app.domain.dto.trainer.TrainerResponse;
import com.gym.crm.app.domain.dto.TrainingDto;
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
        List<TrainerResponse> expected = data.getTrainers().stream()
                .map(trainer -> modelMapper.map(trainer, TrainerResponse.class))
                .toList();

        when(trainerService.getAllTrainers()).thenReturn(expected);

        List<TrainerResponse> actual = gymFacade.getAllTrainers();

        verify(trainerService).getAllTrainers();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        TrainerResponse expected = modelMapper.map(data.getTrainers().get(0), TrainerResponse.class);
        when(trainerService.getTrainerByUsername("username")).thenReturn(expected);

        TrainerResponse actual = gymFacade.getTrainerByUsername("username");

        verify(trainerService).getTrainerByUsername("username");
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddTrainer() {
        TrainerResponse trainerResponse = modelMapper.map(data.getTrainers().get(0), TrainerResponse.class);
        when(trainerService.addTrainer(trainerResponse)).thenReturn(trainerResponse);

        TrainerResponse actual = gymFacade.addTrainer(trainerResponse);

        verify(trainerService).addTrainer(trainerResponse);
        assertEquals(trainerResponse, actual);
    }

    @Test
    void shouldUpdateTrainerByUsername() {
        TrainerResponse trainerResponse = modelMapper.map(data.getTrainers().get(0), TrainerResponse.class);
        when(trainerService.updateTrainerByUsername("username", trainerResponse)).thenReturn(trainerResponse);

        TrainerResponse actual = gymFacade.updateTrainerByUsername("username", trainerResponse);

        verify(trainerService).updateTrainerByUsername("username", trainerResponse);
        assertEquals(trainerResponse, actual);
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        doNothing().when(trainerService).deleteTrainerByUsername("username");

        gymFacade.deleteTrainerByUsername("username");

        verify(trainerService).deleteTrainerByUsername("username");
    }

    @Test
    void shouldReturnAllTrainees() {
        List<TraineeResponse> expected = data.getTrainees().stream()
                .map(trainee -> modelMapper.map(trainee, TraineeResponse.class))
                .toList();

        when(traineeService.getAllTrainees()).thenReturn(expected);

        List<TraineeResponse> actual = gymFacade.getAllTrainees();

        verify(traineeService).getAllTrainees();
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnTraineeByUsername() {
        TraineeResponse expected = modelMapper.map(data.getTrainees().get(0), TraineeResponse.class);
        when(traineeService.getTraineeByUsername("username")).thenReturn(expected);

        TraineeResponse actual = gymFacade.getTraineeByUsername("username");

        verify(traineeService).getTraineeByUsername("username");
        assertEquals(expected, actual);
    }

    @Test
    void shouldAddTrainee() {
        TraineeResponse traineeResponse = modelMapper.map(data.getTrainees().get(0), TraineeResponse.class);
        when(traineeService.addTrainee(traineeResponse)).thenReturn(traineeResponse);

        TraineeResponse actual = gymFacade.addTrainee(traineeResponse);

        verify(traineeService).addTrainee(traineeResponse);
        assertEquals(traineeResponse, actual);
    }

    @Test
    void shouldUpdateTraineeByUsername() {
        TraineeResponse traineeResponse = modelMapper.map(data.getTrainees().get(0), TraineeResponse.class);
        when(traineeService.updateTraineeByUsername("username", traineeResponse)).thenReturn(traineeResponse);

        TraineeResponse actual = gymFacade.updateTraineeByUsername("username", traineeResponse);

        verify(traineeService).updateTraineeByUsername("username", traineeResponse);
        assertEquals(traineeResponse, actual);
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