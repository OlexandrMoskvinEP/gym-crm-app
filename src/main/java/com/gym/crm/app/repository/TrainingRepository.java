package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    List<Training> findAll();

    Optional<Training> findById(Long id);

    void update(Training training);

    Training save(Training training);

    void deleteById(Long id);

    List<Training> findByTrainerId(Long trainerId);

    List<Training> findByDate(LocalDate date);

    List<Training> findByTraineeId(Long traineeId);

    Optional<Training> findByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);

    void deleteByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);


}
