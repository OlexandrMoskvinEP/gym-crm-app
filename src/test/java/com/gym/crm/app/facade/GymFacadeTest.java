package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.mapper.TraineeMapper;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.mapper.TrainingTypeMapper;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.AvailableTrainerGetResponse;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.TraineeTrainingGetResponse;
import com.gym.crm.app.rest.TraineeUpdateResponse;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerTrainingGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import com.gym.crm.app.rest.TrainingTypeRestDto;
import com.gym.crm.app.rest.TrainingWithTraineeName;
import com.gym.crm.app.rest.TrainingWithTrainerName;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.security.CurrentUserHolder;
import com.gym.crm.app.security.UserRole;
import com.gym.crm.app.security.model.AuthenticatedUser;
import com.gym.crm.app.security.model.UserCredentialsDto;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static com.gym.crm.app.security.UserRole.ADMIN;
import static com.gym.crm.app.security.UserRole.TRAINEE;
import static com.gym.crm.app.security.UserRole.TRAINER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
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
    private static final User SIMPLE_USER = buildSimpleUser();

    private static final TraineeCreateResponse TRAINEE_CREATE_RESPONSE = buildTraineeCreateResponse();
    private static final TrainerCreateResponse TRAINER_CREATE_RESPONSE = buildTrainerCreateResponse();
    private static final TraineeGetResponse TRAINEE_GET_RESPONSE = buildTraineeGetResponse();
    private static final TrainerGetResponse TRAINER_GET_RESPONSE = buildTrainerGetResponse();
    private static final TraineeUpdateResponse TRAINEE_UPDATE_RESPONSE = buildTraineeUpdateResponse();
    private static final TrainerUpdateResponse TRAINER_UPDATE_RESPONSE = buildTrainerUpdateResponse();
    private static final TraineeAssignedTrainersUpdateRequest TRAINEE_ASSIGNED_TRAINERS_UPDATE_REQUEST = buildAssignedTrainerRequest();
    private static final TrainingCreateRequest TRAINING_CREATE_REQUEST = getTrainingCreateRequest();
    private static final TrainingSaveRequest TRAINING_SAVE_REQUEST = getTrainingSaveRequest();

    @Mock
    private HttpServletRequest request;
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
    @Mock
    private HttpSession session;
    @Spy
    private TraineeMapper traineeMapper = Mappers.getMapper(TraineeMapper.class);
    @Spy
    private TrainerMapper trainerMapper = Mappers.getMapper(TrainerMapper.class);
    @Spy
    private TrainingMapper trainingMapper = Mappers.getMapper(TrainingMapper.class);
    @Spy
    private TrainingTypeMapper trainingTypeMapper = Mappers.getMapper(TrainingTypeMapper.class);
    @Spy
    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    @InjectMocks
    private GymFacade facade;

    @BeforeEach
    void setup() {
        when(request.getSession(true)).thenReturn(session);

        lenient().when(request.getSession(false)).thenReturn(session);
        lenient().when(session.getAttribute("AUTHENTICATED_USER")).thenReturn(getAuthenticatedUser());

        CurrentUserHolder currentUserHolder = spy(new CurrentUserHolder(request));
        currentUserHolder.set(getAuthenticatedUser());

        ReflectionTestUtils.setField(facade, "currentUserHolder", currentUserHolder);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<TrainerDto> expected = List.of(TRAINER_DTO);
        when(trainerService.getAllTrainers()).thenReturn(expected);

        List<TrainerDto> actual = facade.getAllTrainers(USER_CREDENTIALS);

        assertEquals(expected, actual);

        verify(trainerService).getAllTrainers();
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN);
    }

    @Test
    void shouldReturnTrainerByUsername() {
        when(trainerService.getTrainerByUsername(USERNAME)).thenReturn(TRAINER_DTO);

        TrainerGetResponse actual = facade.getTrainerByUsername(USERNAME);

        assertEquals(TRAINER_GET_RESPONSE, actual);

        verify(trainerService).getTrainerByUsername(USERNAME);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINER, TRAINEE);
    }

    @Test
    void shouldAddTrainer() {
        TrainerCreateRequest createRequest = TrainerCreateRequest.builder().build();
        when(trainerService.addTrainer(createRequest)).thenReturn(TRAINER_DTO);

        TrainerCreateResponse actual = facade.addTrainer(createRequest);

        assertEquals(TRAINER_CREATE_RESPONSE, actual);

        verify(trainerService).addTrainer(createRequest);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldUpdateTrainerByUsername() {
        TrainerUpdateRequest updateRequest = TrainerUpdateRequest.builder().build();
        when(trainerService.updateTrainerByUsername(USERNAME, updateRequest)).thenReturn(TRAINER_DTO);

        TrainerUpdateResponse actual = facade.updateTrainerByUsername(USERNAME, updateRequest);

        assertEquals(TRAINER_UPDATE_RESPONSE, actual);

        verify(trainerService).updateTrainerByUsername(USERNAME, updateRequest);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINER);
    }

    @Test
    void shouldDeleteTrainerByUsername() {
        doNothing().when(trainerService).deleteTrainerByUsername(USERNAME);

        facade.deleteTrainerByUsername(USERNAME, USER_CREDENTIALS);

        verify(trainerService).deleteTrainerByUsername(USERNAME);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINER);
    }

    @Test
    void shouldReturnAllTrainees() {
        List<TraineeDto> expected = List.of(TRAINEE_DTO);
        when(traineeService.getAllTrainees()).thenReturn(expected);

        List<TraineeDto> actual = facade.getAllTrainees(USER_CREDENTIALS);

        assertEquals(expected, actual);

        verify(traineeService).getAllTrainees();
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN);
    }

    @Test
    void shouldReturnTraineeByUsername() {
        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(TRAINEE_DTO);

        TraineeGetResponse actual = facade.getTraineeByUsername(USERNAME);

        assertEquals(TRAINEE_GET_RESPONSE, actual);
        verify(traineeService).getTraineeByUsername(USERNAME);
    }

    @Test
    void shouldAddTrainee() {
        TraineeCreateRequest createRequest = TraineeCreateRequest.builder().build();
        when(traineeService.addTrainee(createRequest)).thenReturn(TRAINEE_DTO);

        TraineeCreateResponse actual = facade.addTrainee(createRequest);

        assertEquals(TRAINEE_CREATE_RESPONSE, actual);

        verify(traineeService).addTrainee(createRequest);
        verifyNoInteractions(authService);
    }

    @Test
    void shouldUpdateTraineeByUsername() {
        TraineeUpdateRequest updateRequest = TraineeUpdateRequest.builder().build();
        when(traineeService.updateTraineeByUsername(USERNAME, updateRequest)).thenReturn(TRAINEE_DTO);

        TraineeUpdateResponse actual = facade.updateTraineeByUsername(USERNAME, updateRequest);

        assertEquals(TRAINEE_UPDATE_RESPONSE, actual);

        verify(traineeService).updateTraineeByUsername(USERNAME, updateRequest);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINEE);
    }

    @Test
    void shouldDeleteTraineeByUsername() {
        doNothing().when(traineeService).deleteTraineeByUsername(USERNAME);

        facade.deleteTraineeByUsername(USERNAME);

        verify(traineeService).deleteTraineeByUsername(USERNAME);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINEE);
    }

    @Test
    void shouldReturnAllTrainings() {
        List<TrainingDto> expected = List.of(TRAINING_DTO);

        when(trainingService.getAllTrainings()).thenReturn(expected);

        List<TrainingDto> actual = facade.getAllTrainings(USER_CREDENTIALS);

        assertEquals(expected, actual);

        verify(trainingService).getAllTrainings();
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN);
    }

    @Test
    void shouldAddTraining() {
        TraineeDto trainee = new TraineeDto();
        trainee.setUserId(1L);

        TrainerDto trainer = new TrainerDto();
        trainer.setUserId(2L);
        trainer.setSpecialization(TrainingType.builder().trainingTypeName("windsurfing").build());

        TrainingDto expected = new TrainingDto();
        expected.setTrainingName("Yoga");
        expected.setTrainerId(trainer.getUserId());
        expected.setTraineeId(trainee.getUserId());
        expected.setTrainingDuration(BigDecimal.valueOf(1));
        expected.setTrainingType(TrainingType.builder().build());
        expected.setTrainingDate(LocalDate.now());

        when(traineeService.getTraineeByUsername("kevin.jackson")).thenReturn(trainee);
        when(trainerService.getTrainerByUsername("chris.tenet")).thenReturn(trainer);
        when(trainingService.addTraining(any())).thenReturn(expected);

        TrainingDto actual = facade.addTraining(TRAINING_CREATE_REQUEST);

        assertEquals(TRAINING_DTO, actual);
        assertEquals(expected, actual);

        verify(authService).checkUserAuthorisation(any(), any(), any());
        verify(trainingService).addTraining(any(TrainingSaveRequest.class));
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, TRAINER, ADMIN);
    }

    @Test
    void shouldUpdateTraining() {
        TrainingSaveRequest saveRequest = TrainingSaveRequest.builder().build();
        when(trainingService.updateTraining(saveRequest)).thenReturn(TRAINING_DTO);

        TrainingDto actual = facade.updateTraining(saveRequest, USER_CREDENTIALS);

        assertEquals(TRAINING_DTO, actual);
        verify(trainingService).updateTraining(saveRequest);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN);
    }

    @Test
    void shouldChangePassword() {
        String username = "username";
        String password = "password";

        facade.changePassword(new ChangePasswordRequest(username, "", password));

        verify(userProfileService).changePassword(username, "", password);
    }

    @Test
    void shouldReturnUnassignedTrainersByTraineeUsername() {
        List<TrainerDto> trainerDtoList = List.of(TRAINER_DTO);
        AvailableTrainerGetResponse expected = new AvailableTrainerGetResponse(trainerDtoList.stream()
                .map(trainerMapper::toEntity).toList());

        when(traineeService.getUnassignedTrainersByTraineeUsername(anyString())).thenReturn(trainerDtoList);

        AvailableTrainerGetResponse actual = facade.getUnassignedTrainersByTraineeUsername("username");

        assertEquals(expected, actual);

        verify(traineeService).getUnassignedTrainersByTraineeUsername("username");
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINEE);
    }

    @Test
    void shouldUpdateTraineeTrainersList() {
        when(traineeService.updateTraineeTrainersByUsername(anyString(), anyList())).thenReturn(List.of());

        facade.updateTraineeTrainersList("username", TRAINEE_ASSIGNED_TRAINERS_UPDATE_REQUEST);

        verify(traineeService).updateTraineeTrainersByUsername("username", List.of("username"));
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSwitchActivationStatus(boolean isActive) {
        ActivationStatusRequest restRequest = new ActivationStatusRequest(isActive);
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(USERNAME)
                .isActive(isActive)
                .build();

        doNothing().when(userProfileService).switchActivationStatus(activationStatusDto);

        facade.switchActivationStatus(USERNAME, restRequest);

        verify(userProfileService).switchActivationStatus(activationStatusDto);
    }

    private static UserCredentialsDto buildCredentials() {
        return UserCredentialsDto.builder()
                .username("username")
                .password("password")
                .role("ADMIN")
                .build();
    }

    @Test
    void shouldReturnTrainingFoundByTraineeCriteria() {
        TraineeTrainingSearchFilter searchFilter = TraineeTrainingSearchFilter.builder()
                .username(USERNAME)
                .build();
        TrainingWithTrainerName expected = getTrainingWithTrainerName();

        when(trainingService.getTraineeTrainingsByFilter(searchFilter)).thenReturn(List.of(TRAINING_DTO));
        when(trainerService.getTrainerNameById(TRAINING_DTO.getTrainerId())).thenReturn("anna.smith");

        TraineeTrainingGetResponse actual = facade.getTraineeTrainingsByFilter(searchFilter);

        assertEquals(1, actual.getTrainings().size());
        assertEquals(expected, actual.getTrainings().get(0));

        verify(trainingService).getTraineeTrainingsByFilter(searchFilter);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINER, TRAINEE);
    }

    @Test
    void shouldReturnTrainingFoundByTrainerCriteria() {
        TrainerTrainingSearchFilter searchFilter = TrainerTrainingSearchFilter.builder()
                .username(USERNAME)
                .build();
        TrainingWithTraineeName expected = getTrainingWithTraineeName();

        when(trainingService.getTrainerTrainingsByFilter(searchFilter)).thenReturn(List.of(TRAINING_DTO));
        when(traineeService.getTraineeNameById(TRAINING_DTO.getTrainerId())).thenReturn("anna.smith");

        TrainerTrainingGetResponse actual = facade.getTrainerTrainingsByFilter(searchFilter);

        assertEquals(1, actual.getTrainings().size());
        assertEquals(expected, actual.getTrainings().get(0));

        verify(trainingService).getTrainerTrainingsByFilter(searchFilter);
        verify(authService).checkUserAuthorisation(USER_CREDENTIALS, ADMIN, TRAINER);
    }

    @Test
    void ShouldReturnAllTrainingsTypes() {
        TrainingTypeGetResponse response = new TrainingTypeGetResponse().trainingTypes(List.of(new TrainingTypeRestDto()));

        when(trainingService.getTrainingTypes()).thenReturn(List.of(TrainingType.builder().build()));

        var actual = facade.getAllTrainingsTypes();

        assertEquals(response.getTrainingTypes(), actual.getTrainingTypes());
        verify(trainingService).getTrainingTypes();
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
                .isActive(true)
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

    private static TraineeCreateResponse buildTraineeCreateResponse() {
        return new TraineeCreateResponse(TRAINEE_DTO.getUsername(), TRAINEE_DTO.getPassword());
    }

    private static TrainerCreateResponse buildTrainerCreateResponse() {
        return new TrainerCreateResponse(TRAINER_DTO.getUsername(), TRAINER_DTO.getPassword());
    }

    private static TraineeGetResponse buildTraineeGetResponse() {
        return new TraineeGetResponse()
                .firstName(TRAINEE_DTO.getFirstName())
                .lastName(TRAINEE_DTO.getLastName())
                .address(TRAINEE_DTO.getAddress())
                .dateOfBirth(TRAINEE_DTO.getDateOfBirth())
                .isActive(TRAINEE_DTO.isActive());
    }

    private static TrainerGetResponse buildTrainerGetResponse() {
        return new TrainerGetResponse()
                .firstName(TRAINER_DTO.getFirstName())
                .lastName(TRAINER_DTO.getLastName())
                .specialization(TRAINER_DTO.getSpecialization().getTrainingTypeName())
                .isActive(TRAINER_DTO.isActive());
    }

    private static TraineeUpdateResponse buildTraineeUpdateResponse() {
        return new TraineeUpdateResponse()
                .firstName(TRAINEE_DTO.getFirstName())
                .lastName(TRAINEE_DTO.getLastName())
                .username(TRAINEE_DTO.getUsername())
                .address(TRAINEE_DTO.getAddress())
                .dateOfBirth(TRAINEE_DTO.getDateOfBirth())
                .isActive(TRAINEE_DTO.isActive());
    }

    private static TrainerUpdateResponse buildTrainerUpdateResponse() {
        return new TrainerUpdateResponse()
                .firstName(TRAINER_DTO.getFirstName())
                .lastName(TRAINER_DTO.getLastName())
                .specialization(TRAINER_DTO.getSpecialization().getTrainingTypeName())
                .isActive(TRAINER_DTO.isActive())
                .username(TRAINER_DTO.getUsername());
    }

    private static User buildSimpleUser() {
        return User.builder().username("username").password("password").build();
    }

    private static TraineeAssignedTrainersUpdateRequest buildAssignedTrainerRequest() {
        List<String> list = List.of("username");

        return new TraineeAssignedTrainersUpdateRequest(list);
    }

    private static AuthenticatedUser buildAuthUser() {
        return AuthenticatedUser.builder()
                .userId(GymFacadeTest.SIMPLE_USER.getId())
                .username(GymFacadeTest.SIMPLE_USER.getUsername())
                .password(GymFacadeTest.SIMPLE_USER.getPassword())
                .isActive(GymFacadeTest.SIMPLE_USER.getIsActive())
                .role(UserRole.valueOf("ADMIN")).build();
    }

    private static TrainingWithTrainerName getTrainingWithTrainerName() {
        TrainingWithTrainerName expected = new TrainingWithTrainerName();
        expected.setTrainingName(TRAINING_DTO.getTrainingName());
        expected.setTrainingDate(TRAINING_DTO.getTrainingDate());
        expected.setTrainingDuration(TRAINING_DTO.getTrainingDuration().intValue());
        expected.setTrainingType(TRAINING_DTO.getTrainingType().getTrainingTypeName());
        expected.setTrainerName("anna.smith");

        return expected;
    }

    private static TrainingWithTraineeName getTrainingWithTraineeName() {
        TrainingWithTraineeName expected = new TrainingWithTraineeName();
        expected.setTrainingName(TRAINING_DTO.getTrainingName());
        expected.setTrainingDate(TRAINING_DTO.getTrainingDate());
        expected.setTrainingDuration(TRAINING_DTO.getTrainingDuration().intValue());
        expected.setTrainingType(TRAINING_DTO.getTrainingType().getTrainingTypeName());
        expected.setTraineeName("anna.smith");

        return expected;
    }

    private AuthenticatedUser getAuthenticatedUser() {
        return AuthenticatedUser.builder()
                .userId(GymFacadeTest.SIMPLE_USER.getId())
                .username("username")
                .password("password")
                .isActive(true)
                .role(ADMIN).build();
    }

    private static TrainingSaveRequest getTrainingSaveRequest() {
        TrainingSaveRequest saveRequest = new TrainingSaveRequest();
        saveRequest.setTrainingName(TRAINING_CREATE_REQUEST.getTrainingName());
        saveRequest.setTrainingDate(TRAINING_CREATE_REQUEST.getTrainingDate());
        saveRequest.setTrainingDuration(BigDecimal.valueOf(TRAINING_CREATE_REQUEST.getTrainingDuration()));
        saveRequest.setTrainingTypeName("windsurfing");
        saveRequest.setTraineeId(1L);
        saveRequest.setTrainerId(2L);

        return saveRequest;
    }

    private static TrainingCreateRequest getTrainingCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainingName("Cardio");
        request.setTrainingDate(LocalDate.of(2025, 7, 22));
        request.setTrainingDuration(60);
        request.setTrainingName("Stretching");
        request.setTraineeUsername("kevin.jackson");
        request.setTrainerUsername("chris.tenet");

        return request;
    }
}
