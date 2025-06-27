package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.TrainingDto;

import java.time.LocalDate;
import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    List<TrainingDto> getTrainingByTrainerId(int trainerId);

    List<TrainingDto> getTrainingByTraineeId(int trainerId);

    List<TrainingDto> getTrainingByDate(LocalDate date);

    List<TrainingDto> getTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

    TrainingDto addTraining(TrainingDto training);

    TrainingDto updateTraining(int trainerId, int traineeId, LocalDate date, TrainingDto trainingDto);

    void deleteTrainingByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

}
