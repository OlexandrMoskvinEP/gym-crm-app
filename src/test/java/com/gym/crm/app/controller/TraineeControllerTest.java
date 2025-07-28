package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.AvailableTrainerGetResponse;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.TraineeTrainingGetResponse;
import com.gym.crm.app.rest.TraineeUpdateResponse;
import com.gym.crm.app.rest.Trainer;
import com.gym.crm.app.rest.TrainingWithTrainerName;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final UserCreateRequest USER_CREATE_REQUEST = buildUserCreateRequest();
    private final TraineeCreateRequest TRAINEE_CREATE_REQUEST = buildTraineeCreateRequest();
    private final TraineeUpdateRequest TRAINEE_UPDATE_REQUEST = buildTraineeUpdateRequest();
    private final AvailableTrainerGetResponse AVAILABLE_TRAINERS_GET_RESPONSE = getAvailableTrainerGetResponse();
    private final TraineeAssignedTrainersUpdateRequest TRAINEE_TRAINERS_UPDATE_REQUEST = getTraineeAssignedTrainersUpdateRequest();
    private final TraineeAssignedTrainersUpdateResponse TRAINEE_TRAINERS_UPDATE_RESPONSE = getTraineeAssignedTrainersUpdateResponse();
    private final TrainingWithTrainerName TRAINING_WITH_TRAINER_NAME = getTrainingWithTrainerName();
    private final String TRAINEE_USERNAME = "olga.ivanova";

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;
    @InjectMocks
    private TraineeController controller;

    @BeforeEach
    void setup() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void shouldRegisterTraineeSuccessfully() throws Exception {
        TraineeCreateResponse response = new TraineeCreateResponse("john.smith", "password123");

        when(facade.addTrainee(TRAINEE_CREATE_REQUEST)).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainees/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINEE_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(facade).addTrainee(TRAINEE_CREATE_REQUEST);
    }

    @Test
    void shouldReturnTraineeProfileSuccessfully() throws Exception {
        Trainer trainer = new Trainer("Arnold", "Schwarzenegger", "Bodybuilding", "Schwarz");
        TraineeGetResponse response = getTraineeGetResponse(trainer);

        when(facade.getTraineeByUsername(TRAINEE_USERNAME)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainees/{username}", TRAINEE_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Olga"))
                .andExpect(jsonPath("$.lastName").value("Ivanova"))
                .andExpect(jsonPath("$.dateOfBirth").value("1998-07-10"))
                .andExpect(jsonPath("$.address").value("Lviv, Ukraine"))
                .andExpect(jsonPath("$.isActive").value(true))
                .andExpect(jsonPath("$.trainer.firstName").value("Arnold"))
                .andExpect(jsonPath("$.trainer.lastName").value("Schwarzenegger"));
    }

    @Test
    void shouldUpdateTraineeSuccessfully() throws Exception {
        TraineeUpdateResponse response = new TraineeUpdateResponse("Olga", "Ivanova");

        when(facade.updateTraineeByUsername(TRAINEE_USERNAME, TRAINEE_UPDATE_REQUEST)).thenReturn(response);

        mockMvc.perform(put("/api/v1/trainees/{username}", TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINEE_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Olga"))
                .andExpect(jsonPath("$.lastName").value("Ivanova"));

        verify(facade).updateTraineeByUsername(TRAINEE_USERNAME, TRAINEE_UPDATE_REQUEST);
    }

    @Test
    void shouldDeleteTraineeProfileSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/trainees/{username}", TRAINEE_USERNAME))
                .andExpect(status().isNoContent());

        verify(facade).deleteTraineeByUsername(TRAINEE_USERNAME);
    }


    @Test
    void shouldReturnAvailableTrainers() throws Exception {
        when(facade.getUnassignedTrainersByTraineeUsername(TRAINEE_USERNAME)).thenReturn(AVAILABLE_TRAINERS_GET_RESPONSE);

        mockMvc.perform(get("/api/v1/trainees/{username}/available-trainers", TRAINEE_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers[0].username").value("arnold.schwarzenegger"))
                .andExpect(jsonPath("$.trainers[0].firstName").value("Arnold"))
                .andExpect(jsonPath("$.trainers[0].lastName").value("Schwarzenegger"))
                .andExpect(jsonPath("$.trainers[0].specialization").value("Bodybuilding"));

        verify(facade).getUnassignedTrainersByTraineeUsername(TRAINEE_USERNAME);
    }

    @Test
    void shouldUpdateTraineeTrainersList() throws Exception {
        when(facade.updateTraineeTrainersList(TRAINEE_USERNAME, TRAINEE_TRAINERS_UPDATE_REQUEST)).thenReturn(TRAINEE_TRAINERS_UPDATE_RESPONSE);

        mockMvc.perform(put("/api/v1/trainees/{username}/trainers", TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINEE_TRAINERS_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainers[0].username").value("john.doe"))
                .andExpect(jsonPath("$.trainers[1].username").value("mike.tyson"))
                .andExpect(jsonPath("$.trainers[0].specialization").value("Cardio"))
                .andExpect(jsonPath("$.trainers[1].specialization").value("Boxing"));

        verify(facade).updateTraineeTrainersList(TRAINEE_USERNAME, TRAINEE_TRAINERS_UPDATE_REQUEST);
    }

    @Test
    void shouldReturnTraineeTrainingsWithFilter() throws Exception {
        TraineeTrainingGetResponse response = new TraineeTrainingGetResponse();
        response.setTrainings(List.of(TRAINING_WITH_TRAINER_NAME));

        when(facade.getTraineeTrainingsByFilter(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainees/{username}/trainings", TRAINEE_USERNAME)
                        .param("fromDate", "2025-07-01")
                        .param("toDate", "2025-07-31")
                        .param("trainerName", "John")
                        .param("trainingType", "Cardio"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainings[0].trainingName").value("Cardio Session"))
                .andExpect(jsonPath("$.trainings[0].trainerName").value("John Doe"));

        verify(facade).getTraineeTrainingsByFilter(argThat(filter ->
                filter.getUsername().equals(TRAINEE_USERNAME) &&
                        filter.getFromDate().equals(LocalDate.of(2025, 7, 1)) &&
                        filter.getToDate().equals(LocalDate.of(2025, 7, 31)) &&
                        filter.getTrainerFullName().equals("John") &&
                        filter.getTrainingTypeName().equals("Cardio")
        ));
    }

    @Test
    void changeTraineeActivationStatus() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest();
        request.setIsActive(true);

        mockMvc.perform(patch("/api/v1/trainees/{username}/change-activation-status", TRAINEE_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).switchActivationStatus(eq(TRAINEE_USERNAME), any());
    }

    private UserCreateRequest buildUserCreateRequest() {
        return new UserCreateRequest("John", "Smith", true);
    }

    private TraineeCreateRequest buildTraineeCreateRequest() {
        return TraineeCreateRequest.builder()
                .user(USER_CREATE_REQUEST)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv, Ukraine")
                .build();
    }

    private static TraineeGetResponse getTraineeGetResponse(Trainer trainer) {
        TraineeGetResponse response = new TraineeGetResponse();
        response.setFirstName("Olga");
        response.setLastName("Ivanova");
        response.setDateOfBirth(LocalDate.of(1998, 7, 10));
        response.setAddress("Lviv, Ukraine");
        response.setIsActive(true);
        response.setTrainer(trainer);

        return response;
    }

    private TraineeUpdateRequest buildTraineeUpdateRequest() {
        return TraineeUpdateRequest.builder()
                .user(USER_CREATE_REQUEST)
                .dateOfBirth(LocalDate.of(1998, 7, 10))
                .address("Updated Address")
                .build();
    }

    private AvailableTrainerGetResponse getAvailableTrainerGetResponse() {
        AvailableTrainerGetResponse response = new AvailableTrainerGetResponse();

        Trainer trainer = new Trainer();
        trainer.setFirstName("Arnold");
        trainer.setLastName("Schwarzenegger");
        trainer.setUsername("arnold.schwarzenegger");
        trainer.setSpecialization("Bodybuilding");

        response.setTrainers(List.of(trainer));

        return response;
    }

    private TraineeAssignedTrainersUpdateRequest getTraineeAssignedTrainersUpdateRequest() {
        List<String> usernames = List.of("john.doe", "mike.tyson");

        TraineeAssignedTrainersUpdateRequest request = new TraineeAssignedTrainersUpdateRequest();
        request.setTrainerUsernames(usernames);

        return request;
    }

    private static TraineeAssignedTrainersUpdateResponse getTraineeAssignedTrainersUpdateResponse() {
        Trainer trainer1 = new Trainer();
        trainer1.setFirstName("John");
        trainer1.setLastName("Doe");
        trainer1.setSpecialization("Cardio");
        trainer1.username("john.doe");

        Trainer trainer2 = new Trainer();
        trainer2.setFirstName("Mike");
        trainer2.setLastName("tyson");
        trainer2.setSpecialization("Boxing");
        trainer2.username("mike.tyson");

        TraineeAssignedTrainersUpdateResponse response = new TraineeAssignedTrainersUpdateResponse();
        response.setTrainers(List.of(trainer1, trainer2));

        return response;
    }

    private static TrainingWithTrainerName getTrainingWithTrainerName() {
        TrainingWithTrainerName training = new TrainingWithTrainerName();
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 7, 10));
        training.setTrainingType("Cardio");
        training.setTrainingDuration(60);
        training.setTrainerName("John Doe");

        return training;
    }
}