package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.exception.UnacceptableOperationException;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    List<TrainingDto> getTrainingByTrainerId(int trainerId);

    List<TrainingDto> getTrainingByTraineeId(int trainerId);

    List<TrainingDto> getTrainingByDate(LocalDate date);

    List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

    TrainingDto addTraining(TrainingDto training) throws UnacceptableOperationException;

    TrainingDto updateTraining(int trainerId, int traineeId, LocalDate date, TrainingDto trainingDto) throws UnacceptableOperationException;

    void deleteTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) throws UnacceptableOperationException;

}
