package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.repository.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.search.filters.TrainerTrainingSearchFilter;

import java.util.List;

public interface TrainingRepository {
    List<Training> findAll();

    void update(Training training);

    Training save(Training training);

    List<Training> findByTrainerCriteria(TrainerTrainingSearchFilter filter);

    List<Training> findByTraineeCriteria(TraineeTrainingSearchFilter filter);
}
