package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.training.TrainingResponse;

import java.util.List;

public interface TrainingService {
    List<TrainingResponse> getAllTrainings();

    TrainingResponse addTraining(TrainingResponse training);

    TrainingResponse updateTraining(TrainingResponse trainingResponse);
}
