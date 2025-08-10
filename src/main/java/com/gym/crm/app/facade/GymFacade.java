package com.gym.crm.app.facade;

import com.gym.crm.app.domain.dto.trainee.TraineeCreateRequest;
import com.gym.crm.app.domain.dto.trainee.TraineeDto;
import com.gym.crm.app.domain.dto.trainee.TraineeUpdateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerCreateRequest;
import com.gym.crm.app.domain.dto.trainer.TrainerDto;
import com.gym.crm.app.domain.dto.trainer.TrainerUpdateRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.user.ChangeActivationStatusDto;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.mapper.TraineeMapper;
import com.gym.crm.app.mapper.TrainerMapper;
import com.gym.crm.app.mapper.TrainingMapper;
import com.gym.crm.app.mapper.TrainingTypeMapper;
import com.gym.crm.app.mapper.UserMapper;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.rest.ActivationStatusRequest;
import com.gym.crm.app.rest.AvailableTrainerGetResponse;
import com.gym.crm.app.rest.ChangePasswordRequest;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.app.rest.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.app.rest.TraineeCreateResponse;
import com.gym.crm.app.rest.TraineeGetResponse;
import com.gym.crm.app.rest.TraineeTrainingGetResponse;
import com.gym.crm.app.rest.TraineeUpdateResponse;
import com.gym.crm.app.rest.Trainer;
import com.gym.crm.app.rest.TrainerCreateResponse;
import com.gym.crm.app.rest.TrainerGetResponse;
import com.gym.crm.app.rest.TrainerTrainingGetResponse;
import com.gym.crm.app.rest.TrainerUpdateResponse;
import com.gym.crm.app.rest.TrainingCreateRequest;
import com.gym.crm.app.rest.TrainingTypeGetResponse;
import com.gym.crm.app.rest.TrainingWithTraineeName;
import com.gym.crm.app.rest.TrainingWithTrainerName;
import com.gym.crm.app.service.TraineeService;
import com.gym.crm.app.service.TrainerService;
import com.gym.crm.app.service.TrainingService;
import com.gym.crm.app.service.common.UserProfileService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Getter
@RequiredArgsConstructor
@Component
public class GymFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserProfileService userProfileService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final TrainingTypeMapper trainingTypeMapper;
    private final UserMapper userMapper;

    public TrainerCreateResponse addTrainer(@Valid TrainerCreateRequest createRequest) {
        return trainerMapper.toCreateResponse(trainerService.addTrainer(createRequest));
    }

    public TraineeCreateResponse addTrainee(@Valid TraineeCreateRequest createRequest) {
        return traineeMapper.dtoToCreateResponse(traineeService.addTrainee(createRequest));
    }

    public List<TrainerDto> getAllTrainers() {
        return trainerService.getAllTrainers();
    }

    public List<TraineeDto> getAllTrainees() {
        return traineeService.getAllTrainees();
    }

    public TrainerGetResponse getTrainerByUsername(String username) {
        return trainerMapper.toGetResponse(trainerService.getTrainerByUsername(username));
    }

    public TraineeGetResponse getTraineeByUsername(String username) {
        return traineeMapper.dtoToGetResponse(traineeService.getTraineeByUsername(username));
    }

    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(String username,
                                                                           TraineeAssignedTrainersUpdateRequest request) {
        List<Trainer> trainers = traineeService.updateTraineeTrainersByUsername(username, request.getTrainerUsernames()).stream()
                .map(trainerMapper::entityToRestTrainer).toList();

        return new TraineeAssignedTrainersUpdateResponse(trainers);
    }

    public TraineeUpdateResponse updateTraineeByUsername(String username,
                                                         @Valid TraineeUpdateRequest updateRequest) {
        return traineeMapper.dtoToUpdateResponse(traineeService.updateTraineeByUsername(username, updateRequest));
    }

    public TrainerUpdateResponse updateTrainerByUsername(String username,
                                                         @Valid TrainerUpdateRequest updateRequest) {
        return trainerMapper.toUpdateResponse(trainerService.updateTrainerByUsername(username, updateRequest));
    }

    public void deleteTrainerByUsername(String username) {
        trainerService.deleteTrainerByUsername(username);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public TrainerTrainingGetResponse getTrainerTrainingsByFilter(@Valid TrainerTrainingSearchFilter criteria) {
        List<TrainingDto> trainings = trainingService.getTrainerTrainingsByFilter(criteria);

        List<TrainingWithTraineeName> trainingWithTraineeNames = trainings.stream()
                .map(this::buildTrainingWithTraineeName)
                .toList();

        return new TrainerTrainingGetResponse(trainingWithTraineeNames);
    }

    public TraineeTrainingGetResponse getTraineeTrainingsByFilter(@Valid TraineeTrainingSearchFilter filter) {
        List<TrainingDto> trainings = trainingService.getTraineeTrainingsByFilter(filter);

        List<TrainingWithTrainerName> trainingWithTrainerNames = trainings.stream()
                .map(this::buildTrainingWithTrainerName)
                .toList();

        return new TraineeTrainingGetResponse(trainingWithTrainerNames);
    }

    public AvailableTrainerGetResponse getUnassignedTrainersByTraineeUsername(String username) {
        List<Trainer> trainers = traineeService.getUnassignedTrainersByTraineeUsername(username).stream()
                .map(trainerMapper::toEntity).toList();

        return new AvailableTrainerGetResponse(trainers);
    }

    public List<TrainingDto> getAllTrainings() {
        return trainingService.getAllTrainings();
    }

    public TrainingDto addTraining(@Valid TrainingCreateRequest request) {
        TrainerDto trainer = trainerService.getTrainerByUsername(request.getTrainerUsername());
        TraineeDto trainee = traineeService.getTraineeByUsername(request.getTraineeUsername());

        TrainingSaveRequest saveRequest = new TrainingSaveRequest();
        saveRequest.setTrainingName(request.getTrainingName());
        saveRequest.setTrainingDate(request.getTrainingDate());
        saveRequest.setTrainingDuration(BigDecimal.valueOf(request.getTrainingDuration()));
        saveRequest.setTrainingTypeName(trainer.getSpecialization().getTrainingTypeName());
        saveRequest.setTraineeId(trainee.getTraineeId());
        saveRequest.setTrainerId(trainer.getTrainerId());

        return trainingService.addTraining(saveRequest);
    }

    public TrainingTypeGetResponse getAllTrainingsTypes() {
        List<TrainingType> trainingTypes = trainingService.getTrainingTypes();
        var trainingTypesRest = trainingTypes.stream().map(trainingTypeMapper::toRestTrainingType).toList();

        return new TrainingTypeGetResponse().trainingTypes(trainingTypesRest);
    }

    public TrainingDto updateTraining(@Valid TrainingSaveRequest updateRequest) {
        return trainingService.updateTraining(updateRequest);
    }

    public void switchActivationStatus(String username, ActivationStatusRequest request) {
        ChangeActivationStatusDto changeActivation = userMapper.toChangeActivationStatusDto(username, request);

        userProfileService.switchActivationStatus(changeActivation);
    }

    public void changePassword(ChangePasswordRequest request) {
        userProfileService.changePassword(request.getUsername(), request.getOldPassword(), request.getNewPassword());
    }

    private TrainingWithTrainerName buildTrainingWithTrainerName(TrainingDto trainingDto) {
        String trainerName = trainerService.getTrainerNameById(trainingDto.getTrainerId());

        TrainingWithTrainerName trainingWithTrainerName = new TrainingWithTrainerName();

        trainingWithTrainerName.setTrainerName(trainerName);
        trainingWithTrainerName.setTrainingName(trainingDto.getTrainingName());
        trainingWithTrainerName.setTrainingDate(trainingDto.getTrainingDate());
        trainingWithTrainerName.setTrainingType(trainingDto.getTrainingType().getTrainingTypeName());
        trainingWithTrainerName.setTrainingDuration(trainingDto.getTrainingDuration().intValue());

        return trainingWithTrainerName;
    }

    private TrainingWithTraineeName buildTrainingWithTraineeName(TrainingDto trainingDto) {
        String traineeName = traineeService.getTraineeNameById(trainingDto.getTrainerId());

        TrainingWithTraineeName trainingWithTraineeName = new TrainingWithTraineeName();

        trainingWithTraineeName.setTraineeName(traineeName);
        trainingWithTraineeName.setTrainingName(trainingDto.getTrainingName());
        trainingWithTraineeName.setTrainingDate(trainingDto.getTrainingDate());
        trainingWithTraineeName.setTrainingType(trainingDto.getTrainingType().getTrainingTypeName());
        trainingWithTraineeName.setTrainingDuration(trainingDto.getTrainingDuration().intValue());

        return trainingWithTraineeName;
    }
}
