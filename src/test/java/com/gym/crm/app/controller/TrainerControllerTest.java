package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.domain.dto.user.UserUpdateRequest;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerTrainingGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import com.gym.crm.app.rest.TrainingWithTraineeName;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final UserCreateRequest USER_CREATE_REQUEST = buildUserCreateRequest();
    private static final TrainerCreateRequest TRAINER_CREATE_REQUEST = buildTrainerCreateRequest();
    private static final String TRAINER_USERNAME = "arnold.schwarzenegger";
    private static final TrainerUpdateRequest TRAINER_UPDATE_REQUEST = buildTrainerUpdateRequest();
    private static final TrainingWithTraineeName TRAINING_WITH_TRAINEE_NAME = buildTrainingWithTraineeName();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;
    @InjectMocks
    private TrainerController controller;

    @BeforeEach
    void setup() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void shouldRegisterTrainerSuccessfully() throws Exception {
        TrainerCreateResponse response = new TrainerCreateResponse("john.smith", "password123");

        when(facade.addTrainer(TRAINER_CREATE_REQUEST)).thenReturn(response);

        mockMvc.perform(post("/api/v1/trainers/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINER_CREATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john.smith"))
                .andExpect(jsonPath("$.password").value("password123"));

        verify(facade).addTrainer(TRAINER_CREATE_REQUEST);
    }

    @Test
    void shouldReturnTrainerProfileSuccessfully() throws Exception {
        TrainerGetResponse response = getTrainerGetResponse();

        when(facade.getTrainerByUsername(TRAINER_USERNAME)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainers/{username}", TRAINER_USERNAME))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Arnold"))
                .andExpect(jsonPath("$.lastName").value("Schwarzenegger"))
                .andExpect(jsonPath("$.specialization").value("swimming"));
    }

    @Test
    void shouldUpdateTrainerSuccessfully() throws Exception {
        TrainerUpdateResponse response = new TrainerUpdateResponse("olga.ivanova", "Olga",
                "Ivanova", "swimming");

        when(facade.updateTrainerByUsername("olga.ivanova", TRAINER_UPDATE_REQUEST)).thenReturn(response);

        mockMvc.perform(put("/api/v1/trainers/{username}", "olga.ivanova")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINER_UPDATE_REQUEST)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Olga"))
                .andExpect(jsonPath("$.lastName").value("Ivanova"));

        verify(facade).updateTrainerByUsername("olga.ivanova", TRAINER_UPDATE_REQUEST);
    }

    @Test
    void shouldReturnTrainerTrainingsWithFilter() throws Exception {
        TrainerTrainingGetResponse response = new TrainerTrainingGetResponse();
        response.setTrainings(List.of(TRAINING_WITH_TRAINEE_NAME));

        when(facade.getTrainerTrainingsByFilter(any())).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainers/{username}/trainings", TRAINER_USERNAME)
                        .param("fromDate", "2025-07-01")
                        .param("toDate", "2025-07-31")
                        .param("traineeName", "Mark Freedman"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainings[0].trainingName").value("Cardio Session"))
                .andExpect(jsonPath("$.trainings[0].traineeName").value("Mark Freedman"));

        verify(facade).getTrainerTrainingsByFilter(argThat(filter ->
                filter.getUsername().equals(TRAINER_USERNAME) &&
                        filter.getFromDate().equals(LocalDate.of(2025, 7, 1)) &&
                        filter.getToDate().equals(LocalDate.of(2025, 7, 31)) &&
                        filter.getTraineeFullName().equals("Mark Freedman")));
    }

    @Test
    void changeTrainerActivationStatus() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest();
        request.setIsActive(true);

        mockMvc.perform(patch("/api/v1/trainers/{username}/change-activation-status", TRAINER_USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).switchActivationStatus(eq(TRAINER_USERNAME), any());
    }

    private static UserCreateRequest buildUserCreateRequest() {
        return UserCreateRequest.builder()
                .firstName("John")
                .lastName("Smith")
                .isActive(true).build();
    }

    private static TrainerCreateRequest buildTrainerCreateRequest() {
        return TrainerCreateRequest.builder()
                .user(USER_CREATE_REQUEST)
                .specialization(TrainingType.builder().trainingTypeName("running").build())
                .build();
    }

    private TrainerGetResponse getTrainerGetResponse() {
        return new TrainerGetResponse("Arnold", "Schwarzenegger", "swimming");
    }

    private static TrainerUpdateRequest buildTrainerUpdateRequest() {
        return TrainerUpdateRequest.builder()
                .user(UserUpdateRequest.builder()
                        .firstName("Olga").lastName("Ivanova").isActive(true).password("123").username("olga.ivanova").build())
                .specialization(TrainingType.builder().trainingTypeName("swimming").build())
                .build();
    }

    private static TrainingWithTraineeName buildTrainingWithTraineeName() {
        TrainingWithTraineeName training = new TrainingWithTraineeName();
        training.setTrainingName("Cardio Session");
        training.setTrainingDate(LocalDate.of(2025, 7, 10));
        training.setTrainingType("Cardio");
        training.setTrainingDuration(60);
        training.setTraineeName("Mark Freedman");

        return training;
    }
}