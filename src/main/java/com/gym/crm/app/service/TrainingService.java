package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    List<TrainingDto> getTrainingByTrainerId(int trainerId);

    List<TrainingDto> getTrainingByTraineeId(int trainerId);

    List<TrainingDto> getTrainingByDate(LocalDate date);

    Optional<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

    TrainingDto addTraining(TrainingDto training);

    TrainingDto updateTraining(TrainingDto trainingDto);

    void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

}
