package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;

import java.util.List;

public interface TraineeService {
    List<TraineeDto> getAllTrainees();

    TraineeDto getTraineeByUsername (String username);

    TraineeDto addTrainee(TraineeCreateRequest traineeCreateRequest);

   TraineeDto updateTraineeByUsername(String username, TraineeUpdateRequest traineeUpdateRequest);

    void deleteTraineeByUsername(String username);
}
