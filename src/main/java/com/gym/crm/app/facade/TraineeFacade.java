package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.TraineeDto;
import com.gym.crm.app.service.TraineeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraineeFacade {
    private final TraineeService traineeService;

    @Autowired
    public TraineeFacade(TraineeService traineeService) {
        this.traineeService = traineeService;
    }

    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public TraineeDto getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public TraineeDto addTrainee(TraineeDto traineeDto) {
        return traineeService.addTrainee(traineeDto);
    }

    public TraineeDto updateTraineeByUsername(String username, TraineeDto traineeDto) {
        return traineeService.updateTraineeByUsername(username, traineeDto);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }
}
