package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.TrainerService;
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
public class TrainerServiceImpl implements TrainerService {
    private static final Logger logger = LoggerFactory.getLogger(TrainerServiceImpl.class);

    private TrainerRepository trainerRepository;
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
    public void setTrainerRepository(TrainerRepository trainerRepository) {
        this.trainerRepository = trainerRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TrainerDto> getAllTrainers() {
        return trainerRepository.findAll()
                .stream()
                .map(trainer -> modelMapper.map(trainer, TrainerDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public TrainerDto getTrainerByUsername(String username) {
        Trainer trainer = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        return modelMapper.map(trainer, TrainerDto.class);
    }

    @Override
    public TrainerDto addTrainer(TrainerDto trainerDto) {
        String username = userProfileService.createUsername(trainerDto.getFirstName(), trainerDto.getLastName());
        String password = passwordService.generatePassword();

        trainerDto.setPassword(password);
        trainerDto.setUsername(username);

        logger.info("Adding trainer with username {}", username);

        Trainer entityToAdd = mapTrainerWithUser(trainerDto);

        Trainer returned =  trainerRepository.saveTrainer(entityToAdd);

        logger.info("Trainer {} successfully added", username);

        return getTrainerDtoFromEntity(returned);
    }

    @Override
    public TrainerDto updateTrainerByUsername(String username, TrainerDto trainerDto) {
        Trainer existing = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        Trainer entityToUpdate = mapTrainerWithUser(trainerDto);

        trainerRepository.saveTrainer(entityToUpdate);

        logger.info("Trainer {} updated", username);

        return modelMapper.map(trainerRepository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public void deleteTrainerByUsername(String username) {
        if (trainerRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainer not found!");
        }

        trainerRepository.deleteByUserName(username);

        logger.info("Trainer {} deleted", username);
    }

    private Trainer mapTrainerWithUser(TrainerDto trainerDto) {
        User user = User.builder()
                .username(trainerDto.getUsername())
                .password(trainerDto.getPassword())
                .isActive(trainerDto.isActive())
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .build();

        return Trainer.builder()
                .specialization(trainerDto.getSpecialization())
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
                trainer.getId()
        );
    }
}
