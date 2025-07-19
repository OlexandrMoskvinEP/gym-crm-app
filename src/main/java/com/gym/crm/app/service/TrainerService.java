package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;

import java.util.List;

public interface TrainerService {
    List<TrainerDto> getAllTrainers();

    TrainerDto getTrainerByUsername(String username);

    TrainerDto addTrainer(TrainerCreateRequest createRequest);

    TrainerDto updateTrainerByUsername(String username, TrainerUpdateRequest updateRequest);

    Long getTrainerIdByUsername(String username);

    String getTrainerNameById(Long id);

    void deleteTrainerByUsername(String username);
}
