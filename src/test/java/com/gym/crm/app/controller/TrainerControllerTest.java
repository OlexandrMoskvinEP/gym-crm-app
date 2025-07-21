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
import com.gym.crm.app.rest.TraineeUpdateResponse;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import jakarta.validation.Valid;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainerControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final TrainerCreateRequest TRAINER_CREATE_REQUEST = buildTrainerCreateRequest();
    private static final UserCreateRequest USER_CREATE_REQUEST = buildUserCreateRequest();
    private static final String TRAINER_USERNAME = "arnold.schwarzenegger";
    private static final TrainerUpdateRequest TRAINER_UPDATE_REQUEST = buildTrainerUpdateRequest();

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
    void getTrainerTrainings() {
    }

    @Test
    void changeTrainerActivationStatus() {
    }

    private static UserCreateRequest buildUserCreateRequest() {
        return new UserCreateRequest("John", "Smith", true);
    }

    private static TrainerCreateRequest buildTrainerCreateRequest() {
        return new TrainerCreateRequest().toBuilder()
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

}