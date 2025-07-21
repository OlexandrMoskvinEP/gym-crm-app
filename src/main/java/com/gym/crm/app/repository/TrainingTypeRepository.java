package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.TrainingType;

import java.util.List;
import java.util.Optional;

public interface TrainingTypeRepository {
    Optional<TrainingType> findByName(String trainingTypeName);

    List<TrainingType> findAll();
}
