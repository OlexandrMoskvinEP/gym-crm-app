package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.CoreServiceException;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    private static final String SIMPLE_USERNAME = "michael.goodman";
    private static final String TRAINEE_EXISTING_USERNAME = "Alice.Smith";
    private static final String TRAINER_EXISTING_USERNAME = "Sophie.Taylor";
    private static final String NEW_USERNAME = "Billie.Eilish";

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


        when(traineeRepository.findAll()).thenReturn(List.of(constructTrainee()));
        when(trainerRepository.findAll()).thenReturn(List.of(constructTrainer()));

        String traineeUsernameGenerated = service.createUsername("Alice", "Smith");
        String trainerUsernameGenerated = service.createUsername("Sophie", "Taylor");

        assertNotNull(traineeUsernameGenerated);
        assertNotEquals(TRAINEE_EXISTING_USERNAME, traineeUsernameGenerated);
        assertNotNull(trainerUsernameGenerated);
        assertNotEquals(TRAINER_EXISTING_USERNAME, trainerUsernameGenerated);
    }

    @Test
    void shouldSuccessfullyChangePassword() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(User.builder().username("username").password("111").build()));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("qwerty321");

        service.changePassword("username", "111", "qwerty321");

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertEquals("qwerty321", savedUser.getPassword());
        assertEquals("username", savedUser.getUsername());
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSuccessfullySwitchUserStatus(boolean targetStatus) {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        User user = constructUser("Ioanne", "Vergilis").toBuilder()
                .isActive(!targetStatus)
                .build();
        String username = user.getUsername();
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(user.getUsername())
                .isActive(targetStatus)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        service.switchActivationStatus(activationStatusDto);
        service.switchActivationStatus(activationStatusDto);

        verify(userRepository, times(2)).save(captor.capture());
        User savedUser = captor.getValue();

        assertEquals(username, savedUser.getUsername());
        assertEquals(targetStatus, savedUser.getIsActive());
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
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionForSwitchingStatusUnexistUser() {
        String username = "Ioanne.Vergilis";
        ChangeActivationStatusDto activationStatusDto = ChangeActivationStatusDto.builder()
                .username(username)
                .isActive(false)
                .build();

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(DataBaseErrorException.class,
                () -> service.switchActivationStatus(activationStatusDto));

        assertEquals("User with username Ioanne.Vergilis not found", ex.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    @ParameterizedTest()
    @ValueSource(strings = {TRAINEE_EXISTING_USERNAME, TRAINER_EXISTING_USERNAME})
    void shouldReturnTrueWhenUsernameAlreadyUsed(String username) {
        when(traineeRepository.findAll()).thenReturn(List.of(constructTrainee()));
        when(trainerRepository.findAll()).thenReturn(List.of(constructTrainer()));

        boolean actual = service.isUsernameAlreadyExists(username);

        assertTrue(actual);
        verify(traineeRepository, never()).findByUserUsername(username);
        verify(trainerRepository, never()).findByUserUsername(username);
        verify(userRepository, never()).findByUsername(username);
    }

    @Test
    void shouldReturnFalseWhenUsernameIsFree() {
        when(traineeRepository.findAll()).thenReturn(List.of(constructTrainee()));
        when(trainerRepository.findAll()).thenReturn(List.of(constructTrainer()));

        boolean actual = service.isUsernameAlreadyExists(NEW_USERNAME);

        assertFalse(actual);
        verify(traineeRepository, never()).findByUserUsername(NEW_USERNAME);
        verify(trainerRepository, never()).findByUserUsername(NEW_USERNAME);
        verify(userRepository, never()).findByUsername(NEW_USERNAME);
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