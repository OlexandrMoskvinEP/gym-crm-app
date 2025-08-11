package com.gym.crm.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.app.exception.AuthorizationErrorException;
import com.gym.crm.app.exception.handling.GlobalExceptionHandler;
import com.gym.crm.app.facade.GymFacade;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.JwtTokenResponse;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.rest.RefreshRequest;
import com.gym.crm.app.security.service.LoginService;
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

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
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
    private GymFacade gymFacade;
    @Mock
    private LoginService loginService;
    @InjectMocks
    private AuthenticateController authenticateController;

    @BeforeEach
    void setup() {
        MappingJackson2HttpMessageConverter converter =
                new MappingJackson2HttpMessageConverter(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(authenticateController)
                .setMessageConverters(converter)
                .setControllerAdvice(new GlobalExceptionHandler(meterRegistry))
                .build();

        lenient().when(meterRegistry.counter(anyString(), any(String[].class)))
                .thenReturn(meterCounter);
    }

    @Test
    void shouldReturn200_AndTokens() throws Exception {
        LoginRequest req = new LoginRequest("john.connor", "Skynet#2029");
        JwtTokenResponse tokens = new JwtTokenResponse();
        tokens.setAccessToken("access-xyz");
        tokens.setRefreshToken("refresh-xyz");

        when(loginService.login(any(LoginRequest.class))).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("access-xyz"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-xyz"));

        verify(loginService).login(any(LoginRequest.class));
    }

    @Test
    void ShouldReturn403_WhenInvalidCredentials() throws Exception {
        LoginRequest req = new LoginRequest("john.connor", "wrong1234567890"); // мин длина ок

        when(loginService.login(any(LoginRequest.class)))
                .thenThrow(new AuthorizationErrorException("Invalid credentials"));

        mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());

        verify(loginService).login(any(LoginRequest.class));
    }

    @Test
    void shouldReturn200_AndNewRefreshToken() throws Exception {
        String oldRefresh = "old-refresh-token";
        JwtTokenResponse tokens = new JwtTokenResponse();
        tokens.setAccessToken("new-access");
        tokens.setRefreshToken("new-refresh");

        when(loginService.refresh(oldRefresh)).thenReturn(tokens);

        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", oldRefresh))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value("new-access"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh"));

        verify(loginService).refresh(oldRefresh);
    }

    @Test
    void shouldReturn4xx_WhenRefreshTokenInvalid() throws Exception {
        String badRefresh = "invalid-refresh-token";

        when(loginService.refresh(badRefresh))
                .thenThrow(new AuthorizationErrorException("Invalid or expired refresh token"));

        mockMvc.perform(post("/api/v1/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("refreshToken", badRefresh))))
                .andExpect(status().isForbidden());

        verify(loginService).refresh(badRefresh);
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

    @Test
    void shouldReturn200AndProvideLogoutSuccessfully() throws Exception {
        RefreshRequest request = new RefreshRequest("someRefreshToken#124457564");

        doNothing().when(loginService).logout(anyString());

        mockMvc.perform(post("/api/v1/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(loginService).logout("someRefreshToken#124457564");
    }

    private static ChangePasswordRequest getChangePasswordRequest() {
        ChangePasswordRequest request = new ChangePasswordRequest();
        request.setUsername("john.smith");
        request.setOldPassword("oldPass123");
        request.setNewPassword("newPass456");

        return request;
    }
}