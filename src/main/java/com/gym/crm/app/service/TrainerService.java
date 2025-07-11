package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainer.TrainerResponse;

import java.util.List;

public interface TrainerService {
    List<TrainerResponse> getAllTrainers();

    TrainerResponse getTrainerByUsername (String username);

    TrainerResponse addTrainer(TrainerResponse trainerResponse);

    TrainerResponse updateTrainerByUsername(String username, TrainerResponse trainerResponse);

    void deleteTrainerByUsername(String username);
}
