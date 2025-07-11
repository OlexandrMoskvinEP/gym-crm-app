package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.training.TrainingSaveRequest;
import com.gym.crm.app.domain.dto.training.TrainingDto;

import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    TrainingDto addTraining(TrainingSaveRequest saveRequest);

    TrainingDto updateTraining(TrainingSaveRequest saveRequest);
}
