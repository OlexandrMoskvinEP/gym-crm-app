package com.gym.crm.app.service.Impl;

import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.service.TraineeService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TraineeServiceImpl implements TraineeService {
    private TraineeRepository traineeRepository;
    private ModelMapper modelMapper;

    @Autowired
    public void setTrainerRepository(TraineeRepository traineeRepository) {
        this.traineeRepository = traineeRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TraineeDto> getAllTrainers() {
        return List.of();
    }

    @Override
    public TraineeDto getTraineeByUsername(String username) {
        return null;
    }

    @Override
    public TraineeDto addTrainee(TraineeDto trainerDto) {
        return null;
    }

    @Override
    public TraineeDto updateTraineeByUsername(String username) {
        return null;
    }

    @Override
    public void deleteTraineeByUsername(String username) {

    }
}
