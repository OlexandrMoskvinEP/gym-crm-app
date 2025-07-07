package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    TrainingDto addTraining(TrainingDto training);

    TrainingDto updateTraining(TrainingDto trainingDto);

    //todo remove methods
    //thees methods are deprecated and will be removed soon
    @Deprecated
    List<TrainingDto> getTrainingByTrainerId(Long trainerId);

    @Deprecated
    List<TrainingDto> getTrainingByTraineeId(Long traineeId);

    @Deprecated
    List<TrainingDto> getTrainingByDate(LocalDate date);

    @Deprecated
    Optional<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

    @Deprecated
    void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

}
