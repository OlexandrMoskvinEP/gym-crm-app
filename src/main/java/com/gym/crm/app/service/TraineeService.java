package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainee.TraineeResponse;

import java.util.List;

public interface TraineeService {
    List<TraineeResponse> getAllTrainees();

    TraineeResponse getTraineeByUsername (String username);

    TraineeResponse addTrainee(TraineeResponse trainerDto);

   TraineeResponse updateTraineeByUsername(String username, TraineeResponse traineeResponse);

    void deleteTraineeByUsername(String username);
}
