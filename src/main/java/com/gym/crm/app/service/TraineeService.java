package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainee.TraineeDto;

import java.util.List;

public interface TraineeService {
    List<TraineeDto> getAllTrainees();

    TraineeDto getTraineeByUsername (String username);

    TraineeDto addTrainee(TraineeDto trainerDto);

   TraineeDto updateTraineeByUsername(String username, TraineeDto traineeDto);

    void deleteTraineeByUsername(String username);
}
