package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticateControllerTest {
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private MockMvc mockMvc;

    @Mock
    private Counter meterCounter;
    @Mock
    private MeterRegistry meterRegistry;
    @Mock
    AuthenticationService authenticationService;
    @Mock
    private GymFacade gymFacade;
    @InjectMocks
    private AuthenticateController authenticateController;

    @BeforeEach
    void setup() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(authenticateController)
                .setMessageConverters(converter)
                .build();

        lenient().when(meterRegistry.counter(anyString(), any(String[].class)))
                .thenReturn(meterCounter);
    }

    @Test
    void shouldReturn2xxOnSuccessfulLogin() throws Exception {
        LoginRequest request = getCorrectLoginRequest();
        String expectedToken = "test.jwt.token";

        when(authenticationService.authenticate(request)).thenReturn(expectedToken);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        verify(authenticationService).authenticate(request);
    }

    @Test
    void shouldChangePasswordSuccessfully() throws Exception {
        ChangePasswordRequest request = getChangePasswordRequest();

        mockMvc.perform(put("/api/v1/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(gymFacade).changePassword(request);
    }

    private static ChangePasswordRequest getChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("john.smith");
        request.setOldPassword("oldPass123");
        request.setNewPassword("newPass456");

        return request;
    }

    private static LoginRequest getCorrectLoginRequest() {
        LoginRequest request = new LoginRequest();
        request.setUsername("john.smith");
        request.setPassword("password123");

        return request;
    }
}