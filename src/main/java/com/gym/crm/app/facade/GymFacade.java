package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeResponse;
import com.gym.crm.app.domain.dto.TrainerDto;
import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    @Autowired
    public GymFacade(TraineeService traineeService, TrainerService trainerService, TrainingService trainingService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
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

    public List<TraineeResponse> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public TraineeResponse getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public TraineeResponse addTrainee(TraineeResponse trainerDto) {
        return traineeService.addTrainee(trainerDto);
    }

    public TraineeResponse updateTraineeByUsername(String username, TraineeResponse traineeResponse) {
        return traineeService.updateTraineeByUsername(username, traineeResponse);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public TrainingDto addTraining(TrainingDto training) {
        return trainingService.addTraining(training);
    }

    public TrainingDto updateTraining(TrainingDto trainingDto) {
        return trainingService.updateTraining(trainingDto);
    }

}
