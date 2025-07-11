package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;

import java.util.List;

public interface TrainingRepository {
    List<Training> findAll();

    void update(Training training);

    Training save(Training training);
}
