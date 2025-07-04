package com.gym.crm.app.service.Impl;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.service.impl.TrainerServiceImpl;
import com.gym.crm.app.service.mapper.TrainerMapper;
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
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private static final TestData data = new TestData();

    private final ModelMapper modelMapper = new ModelMapper();
    private final List<Trainer> trainers = data.getTrainers();

    @Mock
    private TrainerRepository repository;
    @Mock
    private PasswordService passwordService;
    @InjectMocks
    private TrainerServiceImpl trainerService;
    @Mock
    private UserProfileService userProfileService;
    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    @BeforeEach
    void setUp() {
        trainerService.setModelMapper(modelMapper);
    }

    @Test
    void shouldReturnAllTrainers() {
        List<TrainerDto> expected = trainers.stream().map(trainer -> modelMapper.map(trainer, TrainerDto.class)).toList();

        when(repository.findAll()).thenReturn(trainers);

        List<TrainerDto> actual = trainerService.getAllTrainers();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Sophie.Taylor", "James.Wilson", "Olivia.Brown"})
    void shouldReturnTrainerByUsername(String username) {
        Optional<Trainer> entity = trainers.stream().filter(trainer -> trainer.getUser().getUsername().equals(username)).findFirst();
        TrainerDto expected = modelMapper.map(entity, TrainerDto.class);

        when(repository.findByUsername(username)).thenReturn(entity);

        TrainerDto actual = trainerService.getTrainerByUsername(username);

        assertEquals(expected, actual);
    }

//    @ParameterizedTest
//    @MethodSource("getTrainers")
    void shouldAddTrainer(Trainer trainer) {
        TrainerDto expected = modelMapper.map(trainer, TrainerDto.class);

        expected.setPassword("fakePassword1234567");
        expected.setUserId(0);

        Trainer trainerToReturn = TrainerMapper.mapToEntityWithUserId(trainer, expected.getUserId());
        User updatedUser = trainer.getUser().toBuilder()
                .password("fakePassword1234567")
                .build();

        trainer = trainer.toBuilder()
                .user(updatedUser)
                .build();

        String username = trainer.getUser().getFirstName() + "." + trainer.getUser().getLastName();

        when(passwordService.generatePassword()).thenReturn("fakePassword1234567");
        when(userProfileService.createUsername(trainer.getUser().getFirstName(), trainer.getUser().getLastName())).thenReturn(username);

        when(repository.saveTrainer(any(Trainer.class))).thenReturn(trainerToReturn);
        when(repository.findByUsername(username)).thenReturn(Optional.of(trainerToReturn));

        TrainerDto actual = trainerService.addTrainer(modelMapper.map(trainer, TrainerDto.class));

        verify(repository, atLeastOnce()).saveTrainer(trainerCaptor.capture());

        TrainerDto savedTrainer = modelMapper.map(trainerCaptor.getValue(), TrainerDto.class);

        assertNotNull(savedTrainer);
        assertNotNull(actual);
        assertEquals(expected, savedTrainer);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldUpdateTrainerByUsername(Trainer trainer) {
        TrainerDto expected = modelMapper.map(trainer, TrainerDto.class);

        expected.setActive(false);
        expected.setSpecialization(new TrainingType(1L,"fakeSport"));

        Trainer trainerToReturn = TrainerMapper.mapToEntityWithUserId(trainer, expected.getUserId());
        User updatedUser = trainerToReturn.getUser().toBuilder()
                .isActive(expected.isActive())
                .build();

        trainerToReturn = trainerToReturn.toBuilder()
                .user(updatedUser)
                .specialization(expected.getSpecialization())
                .build();

        String username = trainer.getUser().getFirstName() + "." + trainer.getUser().getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(trainerToReturn));
        when(repository.saveTrainer(any(Trainer.class))).thenReturn(trainerToReturn);

        TrainerDto actual = trainerService.updateTrainerByUsername(username, expected);

        verify(repository, atLeastOnce()).saveTrainer(trainerCaptor.capture());

        TrainerDto savedTrainer = modelMapper.map(trainerCaptor.getValue(), TrainerDto.class);

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
}