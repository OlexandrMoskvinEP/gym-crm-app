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

    //thees methods are deprecated and will be removed
    //todo remove methods
    @Deprecated
    List<Training> findByTrainerId(Long trainerId);

    @Deprecated
    List<Training> findByDate(LocalDate date);

    @Deprecated
    List<Training> findByTraineeId(Long traineeId);

    @Deprecated
    Optional<Training> findByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);

    @Deprecated
    void deleteByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);


}
