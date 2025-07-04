package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    List<TrainingDto> getTrainingByTrainerId(Long trainerId);

   List<TrainingDto> getTrainingByTraineeId(Long traineeId);

    List<TrainingDto> getTrainingByDate(LocalDate date);

    Optional<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

    TrainingDto addTraining(TrainingDto training);

    TrainingDto updateTraining(TrainingDto trainingDto);

    void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

}
