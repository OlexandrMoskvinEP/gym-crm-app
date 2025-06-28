package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.PasswordGenerator;
import com.gym.crm.app.service.TraineeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TraineeServiceImpl implements TraineeService {
    private TraineeRepository traineeRepository;
    private ModelMapper modelMapper;
    private PasswordGenerator passwordGenerator;

    @Autowired
    public void setPasswordGenerator(PasswordGenerator passwordGenerator) {
        this.passwordGenerator = passwordGenerator;
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
        String username = passwordGenerator.generateUsername(traineeDto.getFirstName(), traineeDto.getLastName());
        String password = passwordGenerator.generatePassword();
        int id = passwordGenerator.generateTraineeId();

        Trainee entityToAdd = Trainee.builder()
                .firstName(traineeDto.getFirstName())
                .lastName(traineeDto.getLastName())
                .username(username)
                .password(password)
                .isActive(traineeDto.isActive())
                .dateOfBirth(traineeDto.getDateOfBirth())
                .address(traineeDto.getAddress())
                .userId(id)
                .build();

        try {
            traineeRepository.save(entityToAdd);
        } catch (Exception e) {
            entityToAdd.setUsername(passwordGenerator.addIndexWhenDuplicate(username, id));
            traineeRepository.save(entityToAdd);
        }
        return modelMapper.map(traineeRepository.findByUsername(username), TraineeDto.class);
    }

    @Override
    public TraineeDto updateTraineeByUsername(String username, TraineeDto traineeDto) {
        Trainee entityToUpdate = traineeRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Trainee not found!"));

        entityToUpdate.setDateOfBirth(traineeDto.getDateOfBirth());
        entityToUpdate.setAddress(traineeDto.getAddress());
        entityToUpdate.setActive(traineeDto.isActive());

        traineeRepository.save(entityToUpdate);

        return modelMapper.map(traineeRepository.findByUsername(username), TraineeDto.class);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        if (traineeRepository.findByUsername(username).isEmpty()) {
            throw new EntityNotFoundException("Trainee not found!");
        }traineeRepository.deleteByUserName(username);
    }
}
