package com.gym.crm.app.service.impl;

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
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.security.service.AuthenticatedUserService;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);
    private static final String NOT_FOUND_ERROR_RESPONSE = "Trainee with username %s not found";

    private final PasswordService passwordService;
    private final UserProfileService userProfileService;
    private final AuthenticatedUserService authenticatedUserService;
    private final TraineeRepository repository;
    private final TrainerMapper trainerMapper;
    private final TraineeMapper traineeMapper;
    private final TrainerRepository trainerRepository;

    @Setter
    private ModelMapper modelMapper;

    @Override
    public List<TraineeDto> getAllTrainees() {
        return repository.findAll()
                .stream()
                .map(trainee -> modelMapper.map(trainee, TraineeDto.class))
                .toList();
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        Trainee trainee = repository.findByUserUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format(NOT_FOUND_ERROR_RESPONSE, username)));

        return traineeMapper.toDto(trainee);
    }

    @Override
    public TraineeDto addTrainee(TraineeCreateRequest createRequest) {
        String username = userProfileService.createUsername(createRequest.getUser().getFirstName(),
                createRequest.getUser().getLastName());
        String password = passwordService.generatePassword();
        String encodedPassword = passwordService.encodePassword(password);

        if (userProfileService.isUsernameAlreadyExists(username)) {
            throw new RegistrationConflictException("Registration as both trainee and trainer is not allowed");
        }

        logger.info("Adding trainee with username {}", username);
        Trainee entityToAdd = mapTraineeWithUser(createRequest, username, encodedPassword);

        Trainee persistedTrainee = repository.save(entityToAdd);

        logger.info("Trainee was successfully added");

        TraineeDto traineeDto = getTraineeDtoFromEntity(persistedTrainee);
        traineeDto.setPassword(password);

        return traineeDto;
    }

    @Override
    public TraineeDto updateTraineeByUsername(String username, TraineeUpdateRequest updateRequest) {
        Trainee existTrainee = repository.findByUserUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format(NOT_FOUND_ERROR_RESPONSE, username)));

        Trainee entityToUpdate = mapUpdatedTraineeWithUser(updateRequest, existTrainee);

        Trainee persistedTrainee = repository.save(entityToUpdate);
        logger.info("Trainee was successfully updated");

        return modelMapper.map(persistedTrainee, TraineeDto.class);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        if (repository.findByUserUsername(username).isEmpty()) {
            throw new DataBaseErrorException(String.format(NOT_FOUND_ERROR_RESPONSE, username));
        }

        repository.deleteByUserUsername(username);
        logger.info("Trainee was successfully deleted");
    }

    @Override
    public List<TrainerDto> getUnassignedTrainersByTraineeUsername(String username) {
        return repository.findUnassignedTrainersByTraineeUsername(username)
                .stream().map(trainerMapper::toDto).toList();
    }

    @Transactional
    @Override
    public List<Trainer> updateTraineeTrainersByUsername(String username, List<String> usernames) {
        Trainee trainee = repository.findByUserUsername(username)
                .orElseThrow(() -> new DataBaseErrorException(String.format(NOT_FOUND_ERROR_RESPONSE, username)));

        List<Trainer> trainers = trainerRepository.findByUserUsernameIn(usernames);
        if (trainers.size() != usernames.size()) {
            throw new DataBaseErrorException("Some trainer usernames not found");
        }

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(trainers);
        repository.save(trainee);

        return trainers;
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
                trainee.getUser().getIsActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getUser().getId(),
                trainee.getId()
        );
    }
}
