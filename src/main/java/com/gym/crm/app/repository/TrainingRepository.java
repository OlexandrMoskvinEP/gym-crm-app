package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TrainingRepository {
    List<Training> findAll();

    List<Training> findByTrainerId(int trainerId);

    List<Training> findByTraineeId(int trainerId);

    List<Training> findByDate(LocalDate date);

    Optional<Training> findByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

    Training saveTraining(Training training);

    void deleteByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

}
