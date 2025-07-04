package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.common.PasswordService;
import com.gym.crm.app.service.common.UserProfileService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {
    private static final Logger logger = LoggerFactory.getLogger(TraineeServiceImpl.class);

    private TraineeRepository traineeRepository;
    private ModelMapper modelMapper;
    private PasswordService passwordService;
    private UserProfileService userProfileService;

    @Autowired
    public void setUserProfileService(UserProfileService userProfileService) {
        this.userProfileService = userProfileService;
    }

    @Autowired
    public void setPasswordService(PasswordService passwordService) {
        this.passwordService = passwordService;
    }

    @Autowired
    public void setTraineeRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TraineeDto> getAllTrainees() {
        return traineeRepository.findAll()
                .stream()
                .map(trainee -> modelMapper.map(trainee, TraineeDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        return modelMapper.map(trainee, TraineeDto.class);
    }

    @Override
    public TraineeDto addTrainee(TraineeDto traineeDto) {
        String username = userProfileService.createUsername(traineeDto.getFirstName(), traineeDto.getLastName());
        String password = passwordService.generatePassword();

        traineeDto.setPassword(password);
        traineeDto.setUsername(username);

        logger.info("Adding trainee with username {}", username);

        Trainee entityToAdd = getTraineeWithUser(traineeDto);

        traineeRepository.saveTrainee(entityToAdd);

        logger.info("Trainee {} successfully added", username);

        return getTraineeDtoFromEntity(traineeRepository.findByUsername(username).get());
    }

    @Override
    public TraineeDto updateTraineeByUsername(String username, TraineeDto traineeDto) {
        Trainee existing = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found!"));

        Trainee entityToUpdate = getTraineeWithUser(traineeDto);

        traineeRepository.saveTrainee(entityToUpdate);

        logger.info("Trainee {} updated", username);

        return modelMapper.map(traineeRepository.findByUsername(username), TraineeDto.class);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        if (traineeRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found!");
        }

        traineeRepository.deleteByUserName(username);

        logger.info("Trainee {} deleted", username);
    }

    private Trainee getTraineeWithUser(TraineeDto traineeDto) {
        User user = User.builder()
                .username(traineeDto.getUsername())
                .password(traineeDto.getPassword())
                .isActive(traineeDto.isActive())
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .build();

        return Trainee.builder()
                .dateOfBirth(traineeDto.getDateOfBirth())
                .address(traineeDto.getAddress())
                .user(user)
                .build();
    }
    private TraineeDto getTraineeDtoFromEntity(Trainee trainee){
        return new TraineeDto(trainee.getUser().getFirstName(),
                trainee.getUser().getLastName(),
                trainee.getUser().getUsername(),
                trainee.getUser().getPassword(),
                trainee.getUser().isActive(),
                trainee.getDateOfBirth(),
                trainee.getAddress(),
                trainee.getId()
                );
    }
}
