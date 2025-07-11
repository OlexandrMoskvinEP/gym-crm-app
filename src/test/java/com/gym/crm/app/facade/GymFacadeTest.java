package com.gym.crm.app.facade;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
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
        TrainerCreateRequest createRequest = TrainerCreateRequest.builder().build();
        TrainerDto serviceResponse = modelMapper.map(data.getTrainers().get(0), TrainerDto.class);
        when(trainerService.addTrainer(createRequest)).thenReturn(serviceResponse);

        TrainerDto actual = gymFacade.addTrainer(createRequest);

        verify(trainerService).addTrainer(createRequest);
        assertEquals(serviceResponse, actual);
    }

    @Test
    void shouldUpdateTrainerByUsername() {
        TrainerUpdateRequest updateRequest = TrainerUpdateRequest.builder().build();
        TrainerDto trainerDto = modelMapper.map(data.getTrainers().get(0), TrainerDto.class);
        when(trainerService.updateTrainerByUsername("username", updateRequest)).thenReturn(trainerDto);

        TrainerDto actual = gymFacade.updateTrainerByUsername("username", updateRequest);

        verify(trainerService).updateTrainerByUsername("username", updateRequest);
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
        TraineeCreateRequest createRequest = TraineeCreateRequest.builder().build();
        TraineeDto traineeDto = modelMapper.map(data.getTrainees().get(0), TraineeDto.class);
        when(traineeService.addTrainee(createRequest)).thenReturn(traineeDto);

        TraineeDto actual = gymFacade.addTrainee(createRequest);

        verify(traineeService).addTrainee(createRequest);
        assertEquals(traineeDto, actual);
    }

    @Test
    void shouldUpdateTraineeByUsername() {
        TraineeUpdateRequest updateRequest = TraineeUpdateRequest.builder().build();
        TraineeDto traineeDto = modelMapper.map(data.getTrainees().get(0), TraineeDto.class);
        when(traineeService.updateTraineeByUsername("username", updateRequest)).thenReturn(traineeDto);

        TraineeDto actual = gymFacade.updateTraineeByUsername("username", updateRequest);

        verify(traineeService).updateTraineeByUsername("username", updateRequest);
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
        TrainingSaveRequest saveRequest = TrainingSaveRequest.builder().build();
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);
        when(trainingService.addTraining(saveRequest)).thenReturn(trainingDto);

        TrainingDto actual = gymFacade.addTraining(saveRequest);

        verify(trainingService).addTraining(saveRequest);
        assertEquals(trainingDto, actual);
    }

    @Test
    void shouldUpdateTraining() {
        TrainingSaveRequest saveRequest = TrainingSaveRequest.builder().build();
        TrainingDto trainingDto = modelMapper.map(data.getTrainings().get(0), TrainingDto.class);
        when(trainingService.updateTraining(saveRequest)).thenReturn(trainingDto);

        TrainingDto actual = gymFacade.updateTraining(saveRequest);

        verify(trainingService).updateTraining(saveRequest);
        assertEquals(trainingDto, actual);
    }

}