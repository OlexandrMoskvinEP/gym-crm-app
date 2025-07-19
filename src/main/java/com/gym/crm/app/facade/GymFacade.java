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
import com.gym.crm.app.mapper.TraineeMapper;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.rest.*;
import com.gym.crm.app.security.AuthenticationService;
import com.gym.crm.app.security.CurrentUserHolder;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import jakarta.validation.Valid;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserProfileService userProfileService;
    private final AuthenticationService authService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    private final UserMapper userMapper;
    private final CurrentUserHolder currentUserHolder;

    @Autowired
    public GymFacade(TraineeService traineeService,
                     TrainerService trainerService,
                     TrainingService trainingService,
                     UserProfileService userProfileService,
                     AuthenticationService authService,
                     TraineeMapper traineeMapper,
                     TrainerMapper trainerMapper, TrainingMapper trainingMapper,
                     UserMapper userMapper, CurrentUserHolder currentUserHolder) {
        this.traineeService = traineeService;
        this.trainerService = trainerService;
        this.trainingService = trainingService;
        this.userProfileService = userProfileService;
        this.authService = authService;
        this.traineeMapper = traineeMapper;
        this.trainerMapper = trainerMapper;
        this.trainingMapper = trainingMapper;
        this.userMapper = userMapper;
        this.currentUserHolder = currentUserHolder;
    }

    public List<TrainerDto> getAllTrainers(UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainerService.getAllTrainers();
    }

    public TrainerDto getTrainerByUsername(String username, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainerService.getTrainerByUsername(username);
    }

    public List<AvailableTrainerGetResponse> getUnassignedTrainersByTraineeUsername(String username) {
        authService.authenticate(getCurrentCredentials());

        return traineeService.getUnassignedTrainersByTraineeUsername(username).stream()
                .map(trainerMapper::dtoToAvailableTrainerResponse)
                .toList();
    }

    public List<TraineeAssignedTrainersUpdateResponse> updateTraineeTrainersList(String username,
                                                                                 TraineeAssignedTrainersUpdateRequest request) {
        authService.authenticate(getCurrentCredentials());
        traineeService.updateTraineeTrainers(username, getIds(request));

        List<TrainerDto> trainers = getTraineeTrainers(request);

        return trainers.stream()
                .map(trainerMapper::dtoToUpdateAssignedTrainerResponse)
                .collect(Collectors.toList());
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

    public TraineeGetResponse getTraineeByUsername(String username) {
        authService.authenticate(getCurrentCredentials());

        return traineeMapper.dtoToGetResponse(traineeService.getTraineeByUsername(username));
    }

    public TraineeCreateResponse addTrainee(@Valid TraineeCreateRequest createRequest) {
        TraineeDto traineeDto = traineeService.addTrainee(createRequest);

        return traineeMapper.dtoToCreateResponse(traineeDto);
    }

    public TraineeUpdateResponse updateTraineeByUsername(String username,
                                                         @Valid TraineeUpdateRequest updateRequest) {

        authService.authenticate(getCurrentCredentials());

        return traineeMapper.dtoToUpdateResponse(traineeService.updateTraineeByUsername(username, updateRequest));
    }

    public void deleteTraineeByUsername(String username) {
        authService.authenticate(getCurrentCredentials());

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

    public List<TrainingDto> getTrainerTrainingsByFilter(@Valid TrainerTrainingSearchFilter criteria, UserCredentialsDto userCredentials) {
        authService.authenticate(userCredentials);

        return trainingService.getTrainerTrainingsByFilter(criteria);
    }

    public List<TraineeTrainingGetResponse> getTraineeTrainingsByFilter(@Valid TraineeTrainingSearchFilter filter) {
        authService.authenticate(getCurrentCredentials());

        List<TrainingDto> trainings = trainingService.getTraineeTrainingsByFilter(filter);

        return trainings.stream()
                .map(this::buildTraineeTrainingGetResponse)
                .toList();
    }

    private UserCredentialsDto getCurrentCredentials() {
        return userMapper.toCredentialsDto(
                currentUserHolder.get());
    }

    private List<Long> getIds(TraineeAssignedTrainersUpdateRequest request) {
        return request.getTrainerUsernames().stream()
                .map(trainerService::getTrainerIdByUsername)
                .toList();
    }

    private List<TrainerDto> getTraineeTrainers(TraineeAssignedTrainersUpdateRequest request) {
        return request.getTrainerUsernames().stream()
                .map(trainerService::getTrainerByUsername)
                .toList();
    }

    private TraineeTrainingGetResponse buildTraineeTrainingGetResponse(TrainingDto trainingDto) {
        String trainerName = trainerService.getTrainerNameById(trainingDto.getTrainerId());

        TraineeTrainingGetResponse response = trainingMapper.toTraineeTrainingResponse(trainingDto);
        response.setTrainerName(trainerName);

        return response;
    }


}
