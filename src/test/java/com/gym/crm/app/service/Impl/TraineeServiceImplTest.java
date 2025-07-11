package com.gym.crm.app.service.Impl;

import com.gym.crm.app.data.TestData;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.user.UserCreateRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import com.gym.crm.app.service.impl.TraineeServiceImpl;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {
    private static final TestData data = new TestData();

    private final ModelMapper modelMapper = new ModelMapper();
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
        traineeService.setModelMapper(modelMapper);
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
        Optional<Trainee> entity = trainees.stream().filter(trainee -> trainee.getUser().getUsername().equals(username)).findFirst();
        TraineeDto expected = modelMapper.map(entity, TraineeDto.class);

        when(repository.findByUsername(username)).thenReturn(entity);

        TraineeDto actual = traineeService.getTraineeByUsername(username);

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldAddTrainee(Trainee trainee) {
        TraineeCreateRequest createRequest = buildCreateRequest(trainee);
        TraineeDto expected = modelMapper.map(trainee, TraineeDto.class);
        expected.setPassword(trainee.getUser().getPassword());
        expected.setUserId(0L);
        expected.setFirstName(trainee.getUser().getFirstName());
        expected.setLastName(trainee.getUser().getLastName());
        expected.setActive(trainee.getUser().isActive());

        Trainee entityToReturn = mapToEntityWithUserId(trainee, expected.getUserId());
        String username = expected.getFirstName() + "." + expected.getLastName();

        when(passwordService.generatePassword()).thenReturn(trainee.getUser().getPassword());
        when(userProfileService.createUsername(anyString(), anyString())).thenReturn(username);
        when(repository.save(any(Trainee.class))).thenReturn(entityToReturn);

        TraineeDto actual = traineeService.addTrainee(createRequest);

        verify(repository, atLeastOnce()).save(traineeCaptor.capture());
        Trainee savedTrainee = traineeCaptor.getValue();
        assertNotNull(savedTrainee);
        assertNotNull(actual);
        assertEquals(trainee, savedTrainee);
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(username, actual.getUsername());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    }

    @ParameterizedTest
    @MethodSource("getTrainees")
    void shouldUpdateTraineeByUsername(Trainee trainee) {
        TraineeUpdateRequest updateRequest = buildUpdateRequest(trainee);
        TraineeDto expected = modelMapper.map(trainee, TraineeDto.class);
        expected.setActive(false);
        expected.setDateOfBirth(LocalDate.of(1989, 3, 8));
        expected.setAddress("checkedAddress");

        Trainee traineeToReturn = mapToEntityWithUserId(trainee, expected.getUserId());
        User updatedUser = traineeToReturn.getUser().toBuilder()
                .isActive(expected.isActive())
                .build();
        traineeToReturn = traineeToReturn.toBuilder()
                .user(updatedUser)
                .address(expected.getAddress())
                .dateOfBirth(expected.getDateOfBirth())
                .build();
        String username = trainee.getUser().getFirstName() + "." + trainee.getUser().getLastName();

        when(repository.findByUsername(username)).thenReturn(Optional.of(traineeToReturn));
        when(repository.save(any(Trainee.class))).thenReturn(traineeToReturn);

        TraineeDto actual = traineeService.updateTraineeByUsername(username, updateRequest);

        verify(repository, atLeastOnce()).save(traineeCaptor.capture());
        TraineeDto savedTrainee = modelMapper.map(traineeCaptor.getValue(), TraineeDto.class);
        assertNotNull(savedTrainee);
        assertNotNull(actual);
        assertEquals(expected.getUserId(), actual.getUserId());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getPassword(), actual.getPassword());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
    }

    @Test
    void shouldThrowExceptionWhetCantDeleteTraineeByUsername() {
        when(repository.findByUsername("fakeUsername")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> traineeService.deleteTraineeByUsername("fakeUsername"));
    }

    private TraineeCreateRequest buildCreateRequest(Trainee trainee) {
        UserCreateRequest user = UserCreateRequest.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .isActive(trainee.getUser().isActive())
                .build();

        return TraineeCreateRequest.builder()
                .user(user)
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .build();
    }

    private TraineeUpdateRequest buildUpdateRequest(Trainee trainee) {
        UserCreateRequest user = UserCreateRequest.builder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .isActive(trainee.getUser().isActive())
                .build();

        return TraineeUpdateRequest.builder()
                .user(user)
                .address(trainee.getAddress())
                .dateOfBirth(trainee.getDateOfBirth())
                .build();
    }

    private static Stream<Trainee> getTrainees() {
        return data.getTrainees().stream();
    }

    private Trainee mapToEntityWithUserId(Trainee source, Long userId) {
        return source.toBuilder()
                .id(userId)
                .build();
    }
}