package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainer.TrainerDto;

import java.util.List;

public interface TrainerService {
    List<TrainerDto> getAllTrainers();

    TrainerDto getTrainerByUsername (String username);

    TrainerDto addTrainer(TrainerDto trainerDto);

    TrainerDto updateTrainerByUsername(String username, TrainerDto trainerDto);

    void deleteTrainerByUsername(String username);
}
