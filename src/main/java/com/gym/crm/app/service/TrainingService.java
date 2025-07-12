package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.training.TrainingDto;
import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.repository.criteria.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.criteria.search.filters.TrainerTrainingSearchFilter;

import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    TrainingDto addTraining(TrainingSaveRequest saveRequest);

    TrainingDto updateTraining(TrainingSaveRequest saveRequest);

    List<TrainingDto> getTraineeTrainingsByFilter(TraineeTrainingSearchFilter criteria);

    List<TrainingDto> getTrainerTrainingsByFilter(TrainerTrainingSearchFilter filter);

}
