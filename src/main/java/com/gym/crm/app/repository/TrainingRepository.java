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

    //This method will be removed in subsequent MR in scope of GCA-61
    List<Training> findByTrainerId(Long trainerId);

    //This method will be removed in subsequent MR in scope of GCA-61
    List<Training> findByDate(LocalDate date);

    //This method will be removed in subsequent MR in scope of GCA-61
    List<Training> findByTraineeId(Long traineeId);

    //This method will be removed in subsequent MR in scope of GCA-61
    Optional<Training> findByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);

    //This method will be removed in subsequent MR in scope of GCA-61
    void deleteByTrainerAndTraineeAndDate(Long trainerId, Long traineeId, LocalDate date);


}
