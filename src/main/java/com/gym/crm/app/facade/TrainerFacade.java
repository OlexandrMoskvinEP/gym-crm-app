package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.service.TrainerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainerFacade {
    private final TrainerService trainerService;

    @Autowired
    public TrainerFacade(TrainerService trainerService) {
        this.trainerService = trainerService;
    }

    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public TrainerDto getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    public TrainerDto addTrainer(TrainerDto trainerDto) {
        return trainerService.addTrainer(trainerDto);
    }

    public TrainerDto updateTrainerByUsername(String username, TrainerDto trainerDto) {
        return trainerService.updateTrainerByUsername(username, trainerDto);
    }

    public void deleteTrainerByUsername(String username) {
        trainerService.deleteTrainerByUsername(username);
    }
}
