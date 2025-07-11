package com.gym.crm.app.service.common;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {
    private final TestData data = new TestData();

    @Mock
    TraineeRepository traineeRepository;
    @Mock
    TrainerRepository trainerRepository;
    @InjectMocks
    private UserProfileService service;

    @Test
    void shouldCreateCorrectUsername() {
        when(traineeRepository.findAll()).thenReturn(data.getTrainees());
        when(trainerRepository.findAll()).thenReturn(data.getTrainers());

        String traineeUsernameExisting = "Alice.Smith";
        String traineeUsernameGenerated = service.createUsername("Alice", "Smith");

        assertNotNull(traineeUsernameGenerated);
        assertNotEquals(traineeUsernameExisting, traineeUsernameGenerated);

        String trainerUsernameExisting = "Sophie.Taylor";
        String trainerUsernameGenerated = service.createUsername("Sophie", "Taylor");

        assertNotNull(trainerUsernameGenerated);
        assertNotEquals(trainerUsernameExisting, trainerUsernameGenerated);
    }
}