package com.gym.crm.app.service.impl;

import com.gym.crm.app.aspect.tx.CoreTransactional;
import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.exception.RegistrationConflictException;
import com.gym.crm.app.mapper.TraineeMapper;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.rest.LoginRequest;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeRepository repository;
    private ModelMapper modelMapper;
    private TrainerMapper trainerMapper;
    private TraineeMapper traineeMapper;
    private PasswordService passwordService;
    private UserProfileService userProfileService;
    private TrainerService trainerService;
    private AuthenticationService authenticationService;

    @Autowired
    public void setTraineeMapper(TraineeMapper traineeMapper) {
        this.traineeMapper = traineeMapper;
    }

    @Autowired
    public void setAuthenticationService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Autowired
    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Autowired
    public void setRepository(TraineeRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setTrainerService(@Lazy TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
    }

    @Override
    public List<TraineeDto> getAllTrainees() {
        return repository.findAll()
                .stream()
                .map(trainee -> modelMapper.map(trainee, TraineeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        Trainee trainee = repository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format("Trainee with username %s not found", username)));

        return traineeMapper.toDto(trainee);
    }

    @Override
    public TraineeDto addTrainee(TraineeCreateRequest createRequest) {
        String username = userProfileService.createUsername(createRequest.getUser().getFirstName(),
                createRequest.getUser().getLastName());
        String password = passwordService.generatePassword();
        String encodedPassword = passwordService.encodePassword(password);

        if (isDuplicateUsername(username)) {
            throw new RegistrationConflictException("Registration as both trainee and trainer is not allowed");
        }

        logger.info("Adding trainee with username {}", username);
        Trainee entityToAdd = mapTraineeWithUser(createRequest, username, encodedPassword);

        Trainee persistedTrainee = repository.save(entityToAdd);

        logger.info("Trainee {} successfully added", username);
        authenticationService.login(new LoginRequest(username, password));

        TraineeDto traineeDto = getTraineeDtoFromEntity(persistedTrainee);
        traineeDto.setPassword(password);

        return traineeDto;
    }

    @Override
    public TraineeDto updateTraineeByUsername(String username, TraineeUpdateRequest updateRequest) {
        Trainee existTrainee = repository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format("Trainee with username %s not found", username)));

        Trainee entityToUpdate = mapUpdatedTraineeWithUser(updateRequest, existTrainee);

        Trainee persistedTrainee = repository.save(entityToUpdate);
        logger.info("Trainee {} updated", username);

        return modelMapper.map(persistedTrainee, TraineeDto.class);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        if (repository.findByUsername(username).isEmpty()) {
            throw new DataBaseErrorException(String.format("Trainee with username %s not found", username));
        }

        repository.deleteByUsername(username);
        logger.info("Trainee {} deleted", username);
    }

    @Override
    public List<TrainerDto> getUnassignedTrainersByTraineeUsername(String username) {
        return repository.findUnassignedTrainersByTraineeUsername(username)
                .stream().map(trainer -> trainerMapper.toDto(trainer)).toList();
    }

    @Override
    @CoreTransactional
    public void updateTraineeTrainersById(String username, List<Long> trainerIds) {
        repository.updateTraineeTrainersById(username, trainerIds);
    }

    @Override
    public List<Trainer> updateTraineeTrainersByUsername(String username, List<String> usernames) {
        return repository.updateTraineeTrainersByUsername(username, usernames);
    }

    @Override
    public String getTraineeNameById(Long id) {
        Trainee trainee = repository.findById(id).orElseThrow(() -> new DataBaseErrorException(String.format("Trainee with id %s not found", id)));

        return trainee.getUser().getFirstName() + " " + trainee.getUser().getLastName();
    }

    private Trainee mapTraineeWithUser(TraineeCreateRequest createRequest, String username, String password) {
        User user = User.builder()
                .username(username)
                .password(password)
                .firstName(createRequest.getUser().getFirstName())
                .lastName(createRequest.getUser().getLastName())
                .isActive(createRequest.getUser().getIsActive())
                .build();

        return Trainee.builder()
                .address(createRequest.getAddress())
                .user(user)
                .build();
    }

    private Trainee mapUpdatedTraineeWithUser(TraineeUpdateRequest createRequest, Trainee existTrainee) {
        User user = User.builder()
                .firstName(createRequest.getUser().getFirstName())
                .lastName(createRequest.getUser().getLastName())
                .isActive(createRequest.getUser().getIsActive())
                .build();
        String updatedAddress = Optional.ofNullable(createRequest.getAddress())
                .orElse(existTrainee.getAddress());
        LocalDate updatedDateOfBirth = Optional.ofNullable(createRequest.getDateOfBirth())
                .orElse(existTrainee.getDateOfBirth());

        return Trainee.builder()
                .address(updatedAddress)
                .dateOfBirth(updatedDateOfBirth)
                .user(user)
                .build();
    }

    private TraineeDto getTraineeDtoFromEntity(Trainee trainee) {
        return new TraineeDto(trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword(),
                trainee.getUser().isActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getId(),
                trainee.getId()
        );
    }

    private boolean isDuplicateUsername(String username) {
        return trainerService.getAllTrainers().stream()
                .filter(Objects::nonNull)
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }
}
