package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TraineeDto;

import java.util.List;

public interface TraineeService {
    List<TraineeDto> getAllTrainers();

    TraineeDto getTraineeByUsername (String username);

    TraineeDto addTrainee(TraineeDto trainerDto);

    TraineeDto updateTraineeByUsername(String username);

    void deleteTraineeByUsername(String username);
}
