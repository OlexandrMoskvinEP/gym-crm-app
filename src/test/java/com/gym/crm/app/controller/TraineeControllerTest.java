package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.Trainer;
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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final UserCreateRequest USER_CREATE_REQUEST = buildUserCreateRequest();
    private final TraineeCreateRequest TRAINEE_CREATE_REQUEST = buildTraineeCreateRequest();


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
        TraineeCreateResponse expected = new TraineeCreateResponse("john.smith", "password123");

        when(facade.addTrainee(TRAINEE_CREATE_REQUEST)).thenReturn(expected);

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
        String username = "olga.ivanova";

        Trainer trainer = new Trainer("Arnold", "Schwarzenegger", "Bodybuilding", "Schwarz");
        TraineeGetResponse response = getTraineeGetResponse(trainer);

        when(facade.getTraineeByUsername(username)).thenReturn(response);

        mockMvc.perform(get("/api/v1/trainees/{username}", username))
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
    void updateTraineeProfile() {
    }

    @Test
    void deleteTraineeProfile() {
    }

    @Test
    void getAvailableTrainers() {
    }

    @Test
    void updateTraineeTrainers() {
    }

    @Test
    void getTraineeTrainings() {
    }

    @Test
    void changeTraineeActivationStatus() {
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
}