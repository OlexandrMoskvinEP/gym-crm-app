package com.gym.crm.app.service.Impl;

import com.gym.crm.app.TestData;
import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.service.impl.TraineeServiceImpl;
import com.gym.crm.app.service.mapper.TraineeMapper;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    private PasswordService passwordService;
    @Mock
    private UserProfileService userProfileService;
    @InjectMocks
    private TraineeServiceImpl traineeService;

    @BeforeEach
    void setUp() {
        traineeService = new TraineeServiceImpl();
        traineeService.setTraineeRepository(repository);
        traineeService.setModelMapper(modelMapper);
        traineeService.setPasswordService(passwordService);
        traineeService.setUserProfileService(userProfileService);
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
        expected.setUserId(0);

        Trainee EntityToReturn = TraineeMapper.mapToEntityWithUserId(trainee, expected.getUserId());
        EntityToReturn = EntityToReturn.toBuilder().password("fakePassword1234567").build();

        String username = trainee.getFirstName() + "." + trainee.getLastName();

        when(passwordService.generatePassword()).thenReturn("fakePassword1234567");
        when(userProfileService.createUsername(trainee.getFirstName(), trainee.getLastName())).thenReturn(username);

        when(repository.saveTrainee(any(Trainee.class))).thenReturn(EntityToReturn);
        when(repository.findByUsername(username)).thenReturn(Optional.of(EntityToReturn));

        TraineeDto actual = traineeService.addTrainee(modelMapper.map(trainee, TraineeDto.class));

        verify(repository, atLeastOnce()).saveTrainee(traineeCaptor.capture());
        TraineeDto savedTrainee = modelMapper.map(traineeCaptor.getValue(), TraineeDto.class);

        assertNotNull(savedTrainee);
        assertNotNull(actual);
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

        Trainee traineeToReturn = TraineeMapper.mapToEntityWithUserId(trainee, expected.getUserId());
        traineeToReturn = traineeToReturn.toBuilder()
                .isActive(expected.isActive()).address(expected.getAddress())
                .dateOfBirth(expected.getDateOfBirth()).build();

        String username = trainee.getFirstName() + "." + trainee.getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(traineeToReturn));
        when(repository.saveTrainee(any(Trainee.class))).thenReturn(traineeToReturn);

        TraineeDto actual = traineeService.updateTraineeByUsername(username, expected);

        verify(repository, atLeastOnce()).saveTrainee(traineeCaptor.capture());

        TraineeDto savedTrainee = modelMapper.map(traineeCaptor.getValue(), TraineeDto.class);

        assertNotNull(savedTrainee);
        assertNotNull(actual);
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