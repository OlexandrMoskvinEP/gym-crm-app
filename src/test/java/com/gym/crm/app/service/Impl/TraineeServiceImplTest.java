package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.PasswordGenerator;
import com.gym.crm.app.TestData;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private final ModelMapper modelMapper = new ModelMapper();
    private static final TestData data = new TestData();
    private final List<Trainee> trainees = data.getTrainees();

    @Captor
    private ArgumentCaptor<Trainee> traineeCaptor;
    @Mock
    private TraineeRepository repository;
    @Mock
    private PasswordGenerator passwordGenerator;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeRepository(repository);
        traineeService.setModelMapper(modelMapper);
        traineeService.setPasswordGenerator(passwordGenerator);
    }

    @Test
    void shouldReturnAllTrainees() {
        List<TraineeDto> expected = trainees.stream().map(trainee -> modelMapper.map(trainee, TraineeDto.class)).toList();

        when(repository.findAll()).thenReturn(trainees);

        List<TraineeDto> actual = traineeService.getAllTrainees();

        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"Bob.Williams", "Eva.Davis", "Alice.Smith"})
    void shouldGetTraineeByUsername(String username) {
        Optional<Trainee> entity = trainees.stream().filter(trainee -> trainee.getUsername().equals(username)).findFirst();
        TraineeDto expected = modelMapper.map(entity, TraineeDto.class);

        when(repository.findByUsername(username)).thenReturn(entity);

        TraineeDto actual = traineeService.getTraineeByUsername(username);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldAddTrainee(Trainee trainee) {
        TraineeDto expected = modelMapper.map(trainee, TraineeDto.class);
        expected.setPassword("fakePassword1234567");
        expected.setUserId(89);

        String username = trainee.getFirstName() + "." + trainee.getLastName();
        when(passwordGenerator.generatePassword()).thenReturn("fakePassword1234567");
        when(passwordGenerator.generateUsername(trainee.getFirstName(), trainee.getLastName())).thenReturn(username);
        when(passwordGenerator.generateTraineeId()).thenReturn(89);

        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findByUsername(username)).thenReturn(Optional.of(modelMapper.map(expected, Trainee.class)));

        TraineeDto actual = traineeService.addTrainee(modelMapper.map(trainee, TraineeDto.class));

        verify(repository, atLeastOnce()).save(traineeCaptor.capture());
        TraineeDto savedTrainee = modelMapper.map(traineeCaptor.getValue(), TraineeDto.class);

        assertEquals(expected, savedTrainee);
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldUpdateTraineeByUsername(Trainee trainee) {
        TraineeDto expected = modelMapper.map(trainee, TraineeDto.class);
        expected.setActive(false);
        expected.setDateOfBirth(LocalDate.of(1989, 3, 8));
        expected.setAddress("checkedAddress");

        String username = trainee.getFirstName() + "." + trainee.getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(modelMapper.map(trainee, Trainee.class)));
        when(repository.save(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TraineeDto actual = traineeService.updateTraineeByUsername(username, expected);

        verify(repository, atLeastOnce()).save(traineeCaptor.capture());
        TraineeDto savedTrainee = modelMapper.map(traineeCaptor.getValue(), TraineeDto.class);

        assertEquals(expected, savedTrainee);
        assertEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhetCantDeleteTraineeByUsername() {
        when(repository.findByUsername("fakeUsername")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.deleteTraineeByUsername("fakeUsername"));
    }

    public static Stream<Trainee> getTrainees() {
        return data.getTrainees().stream();
    }
}