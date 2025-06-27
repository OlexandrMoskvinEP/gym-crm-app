package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.service.PasswordGenerator;
import com.gym.crm.app.service.TrainerService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService {
    private TrainerRepository trainerRepository;
    private ModelMapper modelMapper;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
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
                .map(t -> modelMapper.map(t, TrainerDto.class))
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
        String username = passwordGenerator.generateUsername(trainerDto.getFirstName(), trainerDto.getLastName());
        String password = passwordGenerator.generatePassword();
        int id = passwordGenerator.generateTrainerId();

        Trainer entityToAdd = Trainer.builder()
                .firstName(trainerDto.getFirstName())
                .lastName(trainerDto.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .specialization(trainerDto.getSpecialization())
                .userId(id)
                .build();

        try {
            trainerRepository.save(entityToAdd);
        } catch (Exception e) {
            entityToAdd.setUsername(passwordGenerator.addIndexWhenDuplicate(username, id));
            trainerRepository.save(entityToAdd);
        }
        return modelMapper.map(trainerRepository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public TrainerDto updateTrainerByUsername(String username, TrainerDto trainerDto) {
        Trainer entityToUpdate = trainerRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("Trainer not found!"));

        entityToUpdate.setFirstName(trainerDto.getFirstName());
        entityToUpdate.setLastName(trainerDto.getLastName());
        entityToUpdate.setSpecialization(trainerDto.getSpecialization());
        entityToUpdate.setActive(trainerDto.isActive());

        trainerRepository.save(entityToUpdate);

        return modelMapper.map(trainerRepository.findByUsername(username), TrainerDto.class);
    }

    @Override
    public void deleteTrainerByUsername(String username) {
        if (trainerRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainer not found!");
        }
    }
}
