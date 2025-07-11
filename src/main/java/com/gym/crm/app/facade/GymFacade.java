package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import jakarta.validation.Valid;
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
    private final UserProfileService userProfileService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     UserProfileService userProfileService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userProfileService = userProfileService;
    }

    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public TrainerDto getTrainerByUsername(String username) {
        return trainerService.getTrainerByUsername(username);
    }

    public TrainerDto addTrainer(@Valid TrainerCreateRequest createRequest) {
        return trainerService.addTrainer(createRequest);
    }

    public TrainerDto updateTrainerByUsername(String username, @Valid TrainerUpdateRequest updateRequest) {
        return trainerService.updateTrainerByUsername(username, updateRequest);
    }

    public void deleteTrainerByUsername(String username) {
        trainerService.deleteTrainerByUsername(username);
    }

    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public TraineeDto getTraineeByUsername(String username) {
        return traineeService.getTraineeByUsername(username);
    }

    public TraineeDto addTrainee(@Valid TraineeCreateRequest createRequest) {
        return traineeService.addTrainee(createRequest);
    }

    public TraineeDto updateTraineeByUsername(String username, @Valid TraineeUpdateRequest updateRequest) {
        return traineeService.updateTraineeByUsername(username, updateRequest);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public TrainingDto addTraining(@Valid TrainingSaveRequest createRequest) {
        return trainingService.addTraining(createRequest);
    }

    public TrainingDto updateTraining(@Valid TrainingSaveRequest updateRequest) {
        return trainingService.updateTraining(updateRequest);
    }

    public void changePassword(String username, String password) {
        userProfileService.changePassword(username, password);
    }

}
