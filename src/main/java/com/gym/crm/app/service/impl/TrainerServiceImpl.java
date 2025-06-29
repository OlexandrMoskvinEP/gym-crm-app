package com.gym.crm.app.service.impl;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.utils.PasswordService;
import com.gym.crm.app.service.utils.UserProfileService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {
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

        Trainer entityToAdd = Trainer.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .username(username)
                .password(password)
                .isActive(trainerDto.isActive())
                .specialization(trainerDto.getSpecialization())
                .build();

        trainerRepository.saveTrainer(entityToAdd);

        return modelMapper.map(trainerRepository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public TrainerDto updateTrainerByUsername(String username, TrainerDto trainerDto) {
        Trainer existing = trainerRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        Trainer entityToUpdate = Trainer.builder()
                .firstName(existing.getFirstName())
                .lastName(existing.getLastName())
                .username(existing.getUsername())
                .password(existing.getPassword())
                .userId(existing.getUserId())
                .specialization(existing.getSpecialization())
                .build();

        trainerRepository.saveTrainer(entityToUpdate);

        return modelMapper.map(trainerRepository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public void deleteTrainerByUsername(String username) {
        if (trainerRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainer not found!");
        }
        trainerRepository.deleteByUserName(username);
    }
}
