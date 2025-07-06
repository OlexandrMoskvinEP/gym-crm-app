package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    TrainingType save(TrainingType trainingType);

    Optional<TrainingType> findById(Long id);

    List<TrainingType> findAll();

    void deleteById(Long id);
}
