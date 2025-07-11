package com.gym.crm.app.service;

import com.gym.crm.app.domain.dto.training.TrainingDto;

import java.util.List;

public interface TrainingService {
    List<TrainingDto> getAllTrainings();

    TrainingDto addTraining(TrainingDto training);

    TrainingDto updateTraining(TrainingDto trainingDto);
}
