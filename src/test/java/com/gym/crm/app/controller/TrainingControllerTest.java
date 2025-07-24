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

import static org.mockito.ArgumentMatchers.any;
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
    private static final TrainingDto TRAINING_DTO = buildTrainingDto();

    private static TrainingCreateRequest TRAINING_CREATE_REQUEST;
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

        TRAINING_CREATE_REQUEST = buildTrainingCreateRequest();
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
        when(facade.addTraining(any(TrainingCreateRequest.class))).thenReturn(TRAINING_DTO);

        mockMvc.perform(post("/api/v1/trainings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(TRAINING_CREATE_REQUEST)))
                .andExpect(status().isOk());

        verify(facade).addTraining(any(TrainingCreateRequest.class));
    }

    private static TrainingTypeGetResponse buildTrainingTypeGetResponse() {
        return new TrainingTypeGetResponse().trainingTypes(
                List.of(new TrainingTypeRestDto().trainingType("fake sport")));
    }

    private static TrainingCreateRequest buildTrainingCreateRequest() {
        return new TrainingCreateRequest()
                .trainingName("Exercise bike riding")
                .trainingDate(LocalDate.now().minusYears(1L))
                .trainingDuration(30)
                .traineeUsername("admin.adminov")
                .trainerUsername("trainer.trainerman");
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