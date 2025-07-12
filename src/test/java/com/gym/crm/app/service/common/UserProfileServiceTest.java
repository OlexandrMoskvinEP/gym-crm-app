package com.gym.crm.app.service.common;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @Mock
    UserRepository userRepository;
    @InjectMocks
    private UserProfileService service;

    @Test
    void shouldCreateCorrectUsername() {
        String traineeUsernameExisting = "Alice.Smith";
        String trainerUsernameExisting = "Sophie.Taylor";

        when(traineeRepository.findAll()).thenReturn(List.of(constructTrainee()));
        when(trainerRepository.findAll()).thenReturn(List.of(constructTrainer()));

        String traineeUsernameGenerated = service.createUsername("Alice", "Smith");

        assertNotNull(traineeUsernameGenerated);
        assertNotEquals(traineeUsernameExisting, traineeUsernameGenerated);

        String trainerUsernameGenerated = service.createUsername("Sophie", "Taylor");

        assertNotNull(trainerUsernameGenerated);
        assertNotEquals(trainerUsernameExisting, trainerUsernameGenerated);
    }

    @Test
    void changePassword() {
        doNothing().when(userRepository).updatePassword(anyString(), anyString());

        userRepository.updatePassword("username", "qwerty321");

        verify(userRepository).updatePassword("username", "qwerty321");
    }

    @Test
    void switchActivationStatus() {
        doNothing().when(userRepository).changeStatus(anyString());

        userRepository.changeStatus("username");

        verify(userRepository).changeStatus("username");
    }

    private Trainer constructTrainer() {
        return Trainer.builder().user(User.builder().firstName("Sophie").lastName("Taylor").username("Sophie.Taylor").build()).build();
    }

    private Trainee constructTrainee() {
        return Trainee.builder().user(User.builder().firstName("Alice").lastName("Smith").username("Alice.Smith").build()).build();
    }
}