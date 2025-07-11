package com.gym.crm.app.service.Impl;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.trainer.TrainerResponse;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final TestData data = new TestData();

    private final ModelMapper modelMapper = new ModelMapper();
    private final List<Trainer> trainers = data.getTrainers();

    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    @Mock
    private TrainerRepository repository;
    @Mock
    private PasswordService passwordService;
    @Mock
    private UserProfileService userProfileService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @BeforeEach
    void setUp() {
        trainerService.setModelMapper(modelMapper);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<TrainerResponse> expected = trainers.stream().map(trainer -> modelMapper.map(trainer, TrainerResponse.class)).toList();

        when(repository.findAll()).thenReturn(trainers);

        List<TrainerResponse> actual = trainerService.getAllTrainers();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sophie.Taylor", "James.Wilson", "Olivia.Brown"})
    void shouldReturnTrainerByUsername(String username) {
        Optional<Trainer> entity = trainers.stream().filter(trainer -> trainer.getUser().getUsername().equals(username)).findFirst();
        TrainerResponse expected = modelMapper.map(entity, TrainerResponse.class);

        when(repository.findByUsername(username)).thenReturn(entity);

        TrainerResponse actual = trainerService.getTrainerByUsername(username);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldAddTrainee(Trainer trainer) {
        TrainerResponse expected = modelMapper.map(trainer, TrainerResponse.class);

        expected.setPassword(trainer.getUser().getPassword());
        expected.setUserId(0L);
        expected.setFirstName(trainer.getUser().getFirstName());
        expected.setLastName(trainer.getUser().getLastName());
        expected.setActive(trainer.getUser().isActive());

        Trainer entityToReturn = mapToEntityWithUserId(trainer, expected.getUserId());

        String username = expected.getFirstName() + "." + expected.getLastName();

        when(passwordService.generatePassword()).thenReturn(trainer.getUser().getPassword());
        when(userProfileService.createUsername(anyString(), anyString())).thenReturn(username);

        when(repository.save(any(Trainer.class))).thenReturn(entityToReturn);

        TrainerResponse actual = trainerService.addTrainer(expected);

        verify(repository, atLeastOnce()).save(trainerCaptor.capture());
        Trainer savedTrainer = trainerCaptor.getValue();

        assertNotNull(savedTrainer);
        assertNotNull(actual);
        assertEquals(trainer, savedTrainer);
        assertEquals(expected, actual);
    }


    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldUpdateTrainerByUsername(Trainer trainer) {
        TrainerResponse expected = modelMapper.map(trainer, TrainerResponse.class);

        expected.setActive(false);
        expected.setSpecialization(new TrainingType(1L, "fakeSport"));

        Trainer trainerToReturn = mapToEntityWithUserId(trainer, expected.getUserId());
        User updatedUser = trainerToReturn.getUser().toBuilder()
                .isActive(expected.isActive())
                .build();

        trainerToReturn = trainerToReturn.toBuilder()
                .user(updatedUser)
                .specialization(expected.getSpecialization())
                .build();

        String username = trainer.getUser().getFirstName() + "." + trainer.getUser().getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(trainerToReturn));
        when(repository.save(any(Trainer.class))).thenReturn(trainerToReturn);

        TrainerResponse actual = trainerService.updateTrainerByUsername(username, expected);

        verify(repository, atLeastOnce()).save(trainerCaptor.capture());

        TrainerResponse savedTrainer = modelMapper.map(trainerCaptor.getValue(), TrainerResponse.class);

        assertNotNull(savedTrainer);
        assertNotNull(actual);
        assertEquals(expected, savedTrainer);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenCantDeleteTrainerByUsername() {
        when(repository.findByUsername("fakeUsername")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.deleteTrainerByUsername("fakeUsername"));
    }

    private static Stream<Trainer> getTrainers() {
        return data.getTrainers().stream();
    }

    private Trainer mapToEntityWithUserId(Trainer source, Long userId) {
        return source.toBuilder()
                .id(userId)
                .build();
    }
}