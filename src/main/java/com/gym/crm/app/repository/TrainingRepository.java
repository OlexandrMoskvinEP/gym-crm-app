package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    List<Training> findAll();

    Optional<Training> findById(Long id);

    Training save(Training training);

    void deleteById(Long id);
}
