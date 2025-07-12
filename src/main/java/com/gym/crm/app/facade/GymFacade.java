package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.user.UserCredentialsDto;
import com.gym.crm.app.repository.criteria.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.criteria.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.security.AuthenticationService;
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
    private final AuthenticationService authService;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     UserProfileService userProfileService,
                     AuthenticationService authService) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userProfileService = userProfileService;
        this.authService = authService;
    }

    public List<TrainerDto> getAllTrainers(UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainerService.getAllTrainers();
    }

    public TrainerDto getTrainerByUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainerService.getTrainerByUsername(username);
    }

    public List<TrainerDto> getUnassignedTrainersByTraineeUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);
        return traineeService.getUnassignedTrainersByTraineeUsername(username);
    }

    public void updateTraineeTrainersList(String username, List<Long> trainerIds, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);
        traineeService.updateTraineeTrainers(username, trainerIds);
    }

    public TrainerDto addTrainer(@Valid TrainerCreateRequest createRequest) {
        return trainerService.addTrainer(createRequest);
    }

    public TrainerDto updateTrainerByUsername(String username,
                                              @Valid TrainerUpdateRequest updateRequest,
                                              UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainerService.updateTrainerByUsername(username, updateRequest);
    }

    public void deleteTrainerByUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        trainerService.deleteTrainerByUsername(username);
    }

    public List<TraineeDto> getAllTrainees(UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return traineeService.getAllTrainees();
    }

    public TraineeDto getTraineeByUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return traineeService.getTraineeByUsername(username);
    }

    public TraineeDto addTrainee(@Valid TraineeCreateRequest createRequest) {
        return traineeService.addTrainee(createRequest);
    }

    public TraineeDto updateTraineeByUsername(String username,
                                              @Valid TraineeUpdateRequest updateRequest,
                                              UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return traineeService.updateTraineeByUsername(username, updateRequest);
    }

    public void deleteTraineeByUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        traineeService.deleteTraineeByUsername(username);
    }

    public List<TrainingDto> getAllTrainings(UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.getAllTrainings();
    }

    public TrainingDto addTraining(@Valid TrainingSaveRequest createRequest, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.addTraining(createRequest);
    }

    public TrainingDto updateTraining(@Valid TrainingSaveRequest updateRequest, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.updateTraining(updateRequest);
    }

    public void switchActivationStatus(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        userProfileService.switchActivationStatus(username);
    }

    public void changePassword(String username, String password, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        userProfileService.changePassword(username, password);
    }

    public List<TrainingDto> getTrainerTrainingsByFilter(TrainerTrainingSearchFilter criteria, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.getTrainerTrainingsByFilter(criteria);
    }

    public List<TrainingDto> getTraineeTrainingsByFilter(TraineeTrainingSearchFilter filter, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.getTraineeTrainingsByFilter(filter);
    }
}
