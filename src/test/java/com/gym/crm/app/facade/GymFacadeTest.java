package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.user.UserCredentialsDto;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GymFacadeTest {
    private static final String FIRST_NAME = "Anna";
    private static final String LAST_NAME = "Smith";
    private static final String USERNAME = "anna.smith";
    private static final TrainingType TRAINING_TYPE = TrainingType.builder().build();
    private static final Long USER_ID = 1L;
    private static final String ADDRESS = "Independence Avenue 57/23";
    private static final LocalDate DATE_OF_BIRTH = LocalDate.parse("1990-12-27");

    private static final TrainerDto TRAINER_DTO = buildTrainerDto();
    private static final TraineeDto TRAINEE_DTO = buildTraineeDto();
    private static final TrainingDto TRAINING_DTO = buildTrainingDto();
    private static final UserCredentialsDto USER_CREDENTIALS = buildCredentials();

    @Mock
    private UserProfileService userProfileService;
    @Mock
    private TraineeService traineeService;
    @Mock
    private TrainerService trainerService;
    @Mock
    private TrainingService trainingService;
    @Mock
    private AuthenticationService authService;

    @InjectMocks
    private GymFacade facade;

    @Test
    void shouldReturnAllTrainers() {
        List<TrainerDto> expected = List.of(TRAINER_DTO);
        when(trainerService.getAllTrainers()).thenReturn(expected);

        List<TrainerDto> actual = facade.getAllTrainers(USER_CREDENTIALS);

        assertEquals(expected, actual);
        verify(trainerService).getAllTrainers();
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        when(trainerService.getTrainerByUsername(USERNAME)).thenReturn(TRAINER_DTO);

        TrainerDto actual = facade.getTrainerByUsername(USERNAME, USER_CREDENTIALS);

        assertEquals(TRAINER_DTO, actual);
        verify(trainerService).getTrainerByUsername(USERNAME);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldAddTrainer() {
        TrainerCreateRequest createRequest = TrainerCreateRequest.builder().build();
        when(trainerService.addTrainer(createRequest)).thenReturn(TRAINER_DTO);

        TrainerDto actual = facade.addTrainer(createRequest);

        assertEquals(TRAINER_DTO, actual);
        verify(trainerService).addTrainer(createRequest);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldUpdateTrainerByUsername() {
        TrainerUpdateRequest updateRequest = TrainerUpdateRequest.builder().build();
        when(trainerService.updateTrainerByUsername(USERNAME, updateRequest)).thenReturn(TRAINER_DTO);

        TrainerDto actual = facade.updateTrainerByUsername(USERNAME, updateRequest, USER_CREDENTIALS);

        assertEquals(TRAINER_DTO, actual);
        verify(trainerService).updateTrainerByUsername(USERNAME, updateRequest);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        doNothing().when(trainerService).deleteTrainerByUsername(USERNAME);

        facade.deleteTrainerByUsername(USERNAME, USER_CREDENTIALS);

        verify(trainerService).deleteTrainerByUsername(USERNAME);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldReturnAllTrainees() {
        List<TraineeDto> expected = List.of(TRAINEE_DTO);
        when(traineeService.getAllTrainees()).thenReturn(expected);

        List<TraineeDto> actual = facade.getAllTrainees(USER_CREDENTIALS);

        assertEquals(expected, actual);
        verify(traineeService).getAllTrainees();
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldReturnTraineeByUsername() {
        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(TRAINEE_DTO);

        TraineeDto actual = facade.getTraineeByUsername(USERNAME, USER_CREDENTIALS);

        assertEquals(TRAINEE_DTO, actual);
        verify(traineeService).getTraineeByUsername(USERNAME);
    }

    @Test
    void shouldAddTrainee() {
        TraineeCreateRequest createRequest = TraineeCreateRequest.builder().build();
        when(traineeService.addTrainee(createRequest)).thenReturn(TRAINEE_DTO);

        TraineeDto actual = facade.addTrainee(createRequest);

        assertEquals(TRAINEE_DTO, actual);
        verify(traineeService).addTrainee(createRequest);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldUpdateTraineeByUsername() {
        TraineeUpdateRequest updateRequest = TraineeUpdateRequest.builder().build();
        when(traineeService.updateTraineeByUsername(USERNAME, updateRequest)).thenReturn(TRAINEE_DTO);

        TraineeDto actual = facade.updateTraineeByUsername(USERNAME, updateRequest, USER_CREDENTIALS);

        assertEquals(TRAINEE_DTO, actual);
        verify(traineeService).updateTraineeByUsername(USERNAME, updateRequest);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        doNothing().when(traineeService).deleteTraineeByUsername(USERNAME);

        facade.deleteTraineeByUsername(USERNAME, USER_CREDENTIALS);

        verify(traineeService).deleteTraineeByUsername(USERNAME);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldReturnAllTrainings() {
        List<TrainingDto> expected = List.of(TRAINING_DTO);

        when(trainingService.getAllTrainings()).thenReturn(expected);

        List<TrainingDto> actual = facade.getAllTrainings(USER_CREDENTIALS);

        assertEquals(expected, actual);
        verify(trainingService).getAllTrainings();
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldAddTraining() {
        TrainingSaveRequest saveRequest = TrainingSaveRequest.builder().build();
        when(trainingService.addTraining(saveRequest)).thenReturn(TRAINING_DTO);

        TrainingDto actual = facade.addTraining(saveRequest, USER_CREDENTIALS);

        assertEquals(TRAINING_DTO, actual);
        verify(trainingService).addTraining(saveRequest);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldUpdateTraining() {
        TrainingSaveRequest saveRequest = TrainingSaveRequest.builder().build();
        when(trainingService.updateTraining(saveRequest)).thenReturn(TRAINING_DTO);

        TrainingDto actual = facade.updateTraining(saveRequest, USER_CREDENTIALS);

        assertEquals(TRAINING_DTO, actual);
        verify(trainingService).updateTraining(saveRequest);
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldChangePassword() {
        String username = "username";
        String password = "password";

        facade.changePassword(username, password, USER_CREDENTIALS);

        verify(userProfileService).changePassword(username, password);
    }

    @Test
    void shouldReturnUnassignedTrainersByTraineeUsername() {
        List<TrainerDto> expected = List.of(TRAINER_DTO);

        when(traineeService.getUnassignedTrainersByTraineeUsername(anyString())).thenReturn(expected);

        List<TrainerDto> actual = facade.getUnassignedTrainersByTraineeUsername("username", USER_CREDENTIALS);

        assertEquals(expected, actual);
        verify(traineeService).getUnassignedTrainersByTraineeUsername("username");
        verify(authService).authenticate(USER_CREDENTIALS);
    }

    @Test
    void shouldUpdateTraineeTrainersList() {
        doNothing().when(traineeService).updateTraineeTrainers(anyString(), anyList());

        facade.updateTraineeTrainersList("username", List.of(1L), USER_CREDENTIALS);

        verify(traineeService).updateTraineeTrainers("username", List.of(1L));
    }

    @Test
    void shouldSwitchActivationStatus() {
        doNothing().when(userProfileService).switchActivationStatus(anyString());

        facade.switchActivationStatus("username", USER_CREDENTIALS);

        verify(userProfileService).switchActivationStatus("username");
    }

    private static UserCredentialsDto buildCredentials() {
        return UserCredentialsDto.builder()
                .username("username")
                .password("password")
                .build();
    }

    private static TrainerDto buildTrainerDto() {
        return TrainerDto.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .userId(USER_ID)
                .specialization(TRAINING_TYPE)
                .build();
    }

    private static TraineeDto buildTraineeDto() {
        return TraineeDto.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .userId(USER_ID)
                .address(ADDRESS)
                .dateOfBirth(DATE_OF_BIRTH)
                .build();
    }

    private static TrainingDto buildTrainingDto() {
        return TrainingDto.builder()
                .traineeId(1L)
                .trainerId(2L)
                .trainingDate(LocalDate.now())
                .trainingDuration(BigDecimal.ONE)
                .trainingName("Yoga")
                .trainingType(TRAINING_TYPE)
                .build();
    }
}