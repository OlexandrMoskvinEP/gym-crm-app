package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import com.gym.crm.app.rest.TrainingTypeRestDto;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TrainingControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final TrainingTypeGetResponse TRAINING_TYPE_GET_RESPONSE = buildTrainingTypeGetResponse();
    private static final TrainingCreateRequest TRAINING_CREATE_REQUEST = buildTraineeCreateRequest();
    private static final TrainingDto TRAINING_DTO = buildTrainingDto();

    private MockMvc mockMvc;

    @Mock
    private GymFacade facade;
    @InjectMocks
    private TrainingController trainingController;

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(trainingController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void getTrainingTypes() throws Exception {
        when(facade.getAllTrainingsTypes()).thenReturn(TRAINING_TYPE_GET_RESPONSE);

        mockMvc.perform(get("/api/v1/training-types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINING_TYPE_GET_RESPONSE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trainingTypes").isArray())
                .andExpect(jsonPath("$.trainingTypes[0].trainingType")
                        .value("fake sport"));
    }

    @Test
    void shouldAddTrainingSuccessfully() throws Exception {
        when(facade.addTraining(TRAINING_CREATE_REQUEST)).thenReturn(TRAINING_DTO);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINING_CREATE_REQUEST)))
                .andExpect(status().isOk());

        verify(facade).addTraining(TRAINING_CREATE_REQUEST);
    }

    @Test
    void getRequestShouldNotBeNull() {
        TrainingController controller = new TrainingController();
        assertNotNull(controller.getRequest());
    }

    private static TrainingTypeGetResponse buildTrainingTypeGetResponse() {
        return new TrainingTypeGetResponse().trainingTypes(
                List.of(new TrainingTypeRestDto().trainingType("fake sport")));
    }

    private static TrainingCreateRequest buildTraineeCreateRequest() {
        TrainingCreateRequest request = new TrainingCreateRequest();
        request.setTrainingName("Exercise bike riding");
        request.setTrainingDate(LocalDate.now().minusYears(1L));
        request.setTrainingDuration(30);
        request.setTraineeUsername("admin.adminov");
        request.setTrainerUsername("trainer.trainerman");

        return request;
    }

    private static TrainingDto buildTrainingDto() {
        return TrainingDto.builder()
                .traineeId(1L)
                .trainerId(2L)
                .trainingDate(LocalDate.now().minusYears(1L))
                .trainingDuration(BigDecimal.valueOf(30))
                .trainingName("Exercise bike riding")
                .trainingType(TrainingType.builder().trainingTypeName("").build())
                .build();
    }
}