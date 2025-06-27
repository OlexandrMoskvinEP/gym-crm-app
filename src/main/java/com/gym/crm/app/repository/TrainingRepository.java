package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;

import java.time.LocalDate;
import java.util.List;

public interface TrainingRepository {
    List<Training> findAll();

    List<Training> findByTrainerId(int trainerId);

    List<Training> findByTraineeId(int trainerId);

    List<Training> findByDate(LocalDate date);

    List<Training> findByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

    Training save(Training training);

    void deleteByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date);

}
