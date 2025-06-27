package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.dto.TrainingIdentityDto;
import com.gym.crm.app.exception.UnacceptableOperationException;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    List<TrainingDto> getTrainingByTrainerId(int trainerId);

    List<TrainingDto> getTrainingByTraineeId(int trainerId);

    List<TrainingDto> getTrainingByDate(LocalDate date);

    List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto);

    TrainingDto addTraining(TrainingDto training) throws UnacceptableOperationException;

    TrainingDto updateTraining(TrainingDto trainingDto) throws UnacceptableOperationException;

    void deleteTrainingByTrainerAndTraineeAndDate(TrainingIdentityDto identityDto) throws UnacceptableOperationException;

}
