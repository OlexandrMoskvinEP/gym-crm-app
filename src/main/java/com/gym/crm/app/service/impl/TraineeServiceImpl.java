package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.trainee.TraineeResponse;
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
    public List<TraineeResponse> getAllTrainees() {
        return traineeRepository.findAll()
                .stream()
                .map(trainee -> modelMapper.map(trainee, TraineeResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public TraineeResponse getTraineeByUsername(String username) {
        Trainee trainee = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        return modelMapper.map(trainee, TraineeResponse.class);
    }

    @Override
    public TraineeResponse addTrainee(TraineeResponse traineeResponse) {
        String username = userProfileService.createUsername(traineeResponse.getFirstName(), traineeResponse.getLastName());
        String password = passwordService.generatePassword();

        traineeResponse.setPassword(password);
        traineeResponse.setUsername(username);

        logger.info("Adding trainee with username {}", username);

        Trainee entityToAdd = getTraineeWithUser(traineeResponse);

        traineeRepository.save(entityToAdd);

        logger.info("Trainee {} successfully added", username);

        return getTraineeDtoFromEntity(traineeRepository.findByUsername(username).get());
    }

    @Override
    public TraineeResponse updateTraineeByUsername(String username, TraineeResponse traineeResponse) {
        Trainee existing = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found!"));

        Trainee entityToUpdate = getTraineeWithUser(traineeResponse);

        traineeRepository.save(entityToUpdate);

        logger.info("Trainee {} updated", username);

        return modelMapper.map(traineeRepository.findByUsername(username), TraineeResponse.class);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        if (traineeRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found!");
        }

        traineeRepository.deleteByUsername(username);

        logger.info("Trainee {} deleted", username);
    }

    private Trainee getTraineeWithUser(TraineeResponse traineeResponse) {
        User user = User.builder()
                .username(traineeResponse.getUsername())
                .password(traineeResponse.getPassword())
                .isActive(traineeResponse.isActive())
                .firstName(traineeResponse.getFirstName())
                .lastName(traineeResponse.getLastName())
                .build();

        return Trainee.builder()
                .dateOfBirth(traineeResponse.getDateOfBirth())
                .address(traineeResponse.getAddress())
                .user(user)
                .build();
    }
    private TraineeResponse getTraineeDtoFromEntity(Trainee trainee){
        return new TraineeResponse(trainee.getUser().getFirstName(),
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
