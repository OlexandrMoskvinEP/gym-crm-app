package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    private static final String USERNAME = "michael.goodman";

    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @InjectMocks
    UserProfileService service;

    @Test
    void shouldCreateCorrectUsername() {
        String traineeUsernameExisting = "Alice.Smith";
        String trainerUsernameExisting = "Sophie.Taylor";

        when(traineeRepository.findAll()).thenReturn(List.of(constructTrainee()));
        when(trainerRepository.findAll()).thenReturn(List.of(constructTrainer()));

        String traineeUsernameGenerated = service.createUsername("Alice", "Smith");
        String trainerUsernameGenerated = service.createUsername("Sophie", "Taylor");

        assertNotNull(traineeUsernameGenerated);
        assertNotEquals(traineeUsernameExisting, traineeUsernameGenerated);
        assertNotNull(trainerUsernameGenerated);
        assertNotEquals(trainerUsernameExisting, trainerUsernameGenerated);
    }

    @Test
    void shouldSuccessfullyChangePassword() {
        doNothing().when(userRepository).updatePassword(anyString(), anyString());
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.builder().password("111").build()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("qwerty321");

        service.changePassword("username", "111", "qwerty321");

        verify(userRepository).updatePassword("username", "qwerty321");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSuccessfullySwitchUserStatus(boolean isActive) {
        User user = constructUser("Ioanne", "Vergilis").toBuilder()
                .isActive(!isActive)
                .build();
        String username = user.getUsername();
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(user.getUsername())
                .isActive(isActive)
                .build();

        doNothing().when(userRepository).changeStatus(anyString());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        service.switchActivationStatus(activationStatusDto);

        verify(userRepository).changeStatus(username);
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldThrowExceptionForChangeStatusIdempotentAction(boolean isActive) {
        User user = constructUser("Ioanne", "Vergilis").toBuilder()
                .isActive(isActive)
                .build();
        String username = user.getUsername();
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(user.getUsername())
                .isActive(isActive)
                .build();
        String status = isActive ? "activate" : "deactivate";
        String expectedMessage = format("Could not %s user %s: user is already %sed", status, username, status);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        CoreServiceException ex = assertThrows(CoreServiceException.class,
                () -> service.switchActivationStatus(activationStatusDto));

        assertEquals(expectedMessage, ex.getMessage());
        verify(userRepository, never()).changeStatus(USERNAME);
    }

    @Test
    void shouldThrowExceptionForSwitchingStatusUnexistUser() {
        String username = "Ioanne.Vergilis";
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(username)
                .isActive(false)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        EntityNotFoundException ex = assertThrows(EntityNotFoundException.class,
                () -> service.switchActivationStatus(activationStatusDto));

        assertEquals("User Ioanne.Vergilis not found", ex.getMessage());
        verify(userRepository, never()).changeStatus(USERNAME);
    }

    private Trainer constructTrainer() {
        return Trainer.builder()
                .user(constructUser("Sophie", "Taylor"))
                .build();
    }

    private Trainee constructTrainee() {
        return Trainee.builder()
                .user(constructUser("Alice", "Smith"))
                .build();
    }

    private User constructUser(String firstName, String lastName) {
        return User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(format("%s.%s", firstName, lastName))
                .build();
    }
}