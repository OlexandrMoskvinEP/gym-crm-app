package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    List<Training> findAll();

    List<Training> findByTrainerId(Long trainerId);

    List<Training> findByDate(LocalDate date);

    Optional<Training> findByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);

    Training saveTraining(Training training);

    void deleteByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);

    List<Training> findByTraineeId(Long traineeId);
}
