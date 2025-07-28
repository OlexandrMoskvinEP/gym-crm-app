package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.exception.RegistrationConflictException;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.repository.TrainerRepository;
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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerRepository repository;
    private ModelMapper modelMapper;
    private PasswordService passwordService;
    private UserProfileService userProfileService;
    private TraineeService traineeService;
    private AuthenticationService authenticationService;
    private TrainerMapper trainerMapper;

    @Autowired
    public void setTrainerMapper(TrainerMapper trainerMapper) {
        this.trainerMapper = trainerMapper;
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
    public void setTraineeService(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    @Autowired
    public void setRepository(TrainerRepository repository) {
        this.repository = repository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        return repository.findAll()
                .stream()
                .map(trainer -> modelMapper.map(trainer, TrainerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        Trainer trainer = repository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format("Trainer with username %s not found", username)));

        return trainerMapper.toDto(trainer);
    }

    @Override
    public TrainerDto addTrainer(TrainerCreateRequest trainerCreateRequest) {
        String username = userProfileService.createUsername(trainerCreateRequest.getUser().getFirstName(), trainerCreateRequest.getUser().getLastName());
        String password = passwordService.generatePassword();
        String encodedPassword = passwordService.encodePassword(password);

        if (isDuplicateUsername(username)) {
            throw new RegistrationConflictException("Registration as both trainer and trainee is not allowed");
        }

        logger.info("Adding trainer with username {}", username);

        Trainer entityToAdd = mapTrainerWithUser(trainerCreateRequest, username, encodedPassword);
        Trainer persistedTrainer = repository.save(entityToAdd);

        logger.info("Trainer {} successfully added", username);
        authenticationService.login(new LoginRequest(username, password));

        TrainerDto trainerDto = getTrainerDtoFromEntity(persistedTrainer);
        trainerDto.setPassword(password);

        return trainerDto;
    }

    @Override
    public TrainerDto updateTrainerByUsername(String username, TrainerUpdateRequest updateRequest) {
        Trainer existTrainer = repository.findByUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format("Trainer with username %s not found", username)));

        Trainer entityToUpdate = mapUpdatedTrainerWithUser(updateRequest, existTrainer);

        repository.save(entityToUpdate);

        logger.info("Trainer {} updated", username);

        return modelMapper.map(repository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public String getTrainerNameById(Long id) {
        Trainer trainer = repository.findById(id).orElseThrow(() -> new DataBaseErrorException(String.format("Trainer with id %s not found", id)));

        return trainer.getUser().getFirstName() + " " + trainer.getUser().getLastName();
    }

    @Override
    public void deleteTrainerByUsername(String username) {
        if (repository.findByUsername(username).isEmpty()) {
            throw new DataBaseErrorException(String.format("Trainer with username %s not found", username));
        }

        repository.deleteByUsername(username);

        logger.info("Trainer {} deleted", username);
    }

    private Trainer mapTrainerWithUser(TrainerCreateRequest createRequest, String username, String password) {
        User user = User.builder()
                .username(username)
                .password(password)
                .firstName(createRequest.getUser().getFirstName())
                .lastName(createRequest.getUser().getLastName())
                .isActive(createRequest.getUser().getIsActive())
                .build();

        return Trainer.builder()
                .specialization(createRequest.getSpecialization())
                .user(user)
                .build();
    }

    private Trainer mapUpdatedTrainerWithUser(TrainerUpdateRequest createRequest, Trainer existTrainer) {
        User user = User.builder()
                .username(existTrainer.getUser().getUsername())
                .password(existTrainer.getUser().getPassword())
                .firstName(createRequest.getUser().getFirstName())
                .lastName(createRequest.getUser().getLastName())
                .isActive(createRequest.getUser().getIsActive())
                .build();

        return Trainer.builder()
                .specialization(createRequest.getSpecialization())
                .user(user)
                .build();
    }

    private TrainerDto getTrainerDtoFromEntity(Trainer trainer) {
        return new TrainerDto(trainer.getUser().getFirstName(),
                trainer.getUser().getLastName(),
                trainer.getUser().getUsername(),
                trainer.getUser().getPassword(),
                trainer.getUser().isActive(),
                trainer.getSpecialization(),
                trainer.getUser().getId(),
                trainer.getId()
        );
    }

    private boolean isDuplicateUsername(String username) {
        return traineeService.getAllTrainees().stream()
                .filter(Objects::nonNull)
                .anyMatch(trainer -> username.equals(trainer.getUsername()));
    }
}
