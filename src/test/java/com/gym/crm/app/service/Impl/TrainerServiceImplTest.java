package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.PasswordGenerator;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TrainerServiceImplTest {
    private final ModelMapper modelMapper = new ModelMapper();
    private static final TestData data = new TestData();
    private final List<Trainer> trainers = data.getTrainers();

    @Mock
    private TrainerRepository repository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @InjectMocks
    private TrainerServiceImpl trainerService;
    @Captor
    private ArgumentCaptor<Trainer> trainerCaptor;

    @BeforeEach
    void setUp() {
        trainerService = new TrainerServiceImpl();
        trainerService.setTrainerRepository(repository);
        trainerService.setModelMapper(modelMapper);
        trainerService.setPasswordGenerator(passwordGenerator);
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
        Optional<Trainer> entity = trainers.stream().filter(trainer -> trainer.getUsername().equals(username)).findFirst();
        TrainerDto expected = modelMapper.map(entity, TrainerDto.class);

        when(repository.findByUsername(username)).thenReturn(entity);

        TrainerDto actual = trainerService.getTrainerByUsername(username);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldAddTrainer(Trainer trainer) {
        TrainerDto expected = modelMapper.map(trainer, TrainerDto.class);
        expected.setPassword("fakePassword1234567");
        expected.setUserId(389);

        String username = trainer.getFirstName() + "." + trainer.getLastName();
        when(passwordGenerator.generatePassword()).thenReturn("fakePassword1234567");
        when(passwordGenerator.generateUsername(trainer.getFirstName(), trainer.getLastName())).thenReturn(username);
        when(passwordGenerator.generateTrainerId()).thenReturn(389);

        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findByUsername(username)).thenReturn(Optional.of(modelMapper.map(expected, Trainer.class)));

        TrainerDto actual = trainerService.addTrainer(modelMapper.map(trainer, TrainerDto.class));

        verify(repository, atLeastOnce()).save(trainerCaptor.capture());
        TrainerDto savedTrainer = modelMapper.map(trainerCaptor.getValue(), TrainerDto.class);

        assertEquals(expected, savedTrainer);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainers")
    void shouldUpdateTrainerByUsername(Trainer trainer) {
        TrainerDto expected = modelMapper.map(trainer, TrainerDto.class);
        expected.setActive(false);
        expected.setSpecialization(new TrainingType("fakeSport"));

        String username = trainer.getFirstName() + "." + trainer.getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(modelMapper.map(trainer, Trainer.class)));
        when(repository.save(any(Trainer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TrainerDto actual = trainerService.updateTrainerByUsername(username, expected);

        verify(repository, atLeastOnce()).save(trainerCaptor.capture());
        TrainerDto savedTrainer = modelMapper.map(trainerCaptor.getValue(), TrainerDto.class);

        assertEquals(expected, savedTrainer);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenCantDeleteTrainerByUsername() {
        when(repository.findByUsername("fakeUsername")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> trainerService.deleteTrainerByUsername("fakeUsername"));
    }

    public static Stream<Trainer> getTrainers() {
        return data.getTrainers().stream();
    }
}