package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.storage.CommonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private Map<String, Training> trainingStorage;

    @Autowired
    public void setTrainingStorage(CommonStorage commonStorage) {
        this.trainingStorage = commonStorage.getTrainingStorage();
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(trainingStorage.values());
    }

    @Override
    public List<Training> findByTrainerId(int trainerId) {
        return findAll().stream()
                .filter(t -> t.getTrainerId() == trainerId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findByTraineeId(int traineeId) {
        return findAll().stream()
                .filter(t -> t.getTraineeId() == traineeId)
                .collect(Collectors.toList());
    }

    @Override
    public List<Training> findByDate(LocalDate date) {
        return findAll().stream()
                .filter(t -> t.getTrainingDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Training> findByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        return findAll().stream()
                .filter(t -> t.getTrainerId() == trainerId)
                .filter(t -> t.getTraineeId() == traineeId)
                .filter(t -> t.getTrainingDate().equals(date))
                .findFirst();
    }

    @Override
    public Training saveTraining(Training training) {
        if (!findByTrainerAndTraineeAndDate(training.getTrainerId(), training.getTraineeId(), training.getTrainingDate()).isEmpty()) {
            throw new DuplicateUsernameException("Entity already exists!");
        }

        String key = training.getTrainerId() + training.getTraineeId() + training.getTrainingDate().toString();

        trainingStorage.put(key, training);

        return trainingStorage.get(key);
    }

    @Override
    public void deleteByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        String key = trainerId + traineeId + date.toString();

        if (!trainingStorage.containsKey(key)) {
            throw new EntityNotFoundException("Unable to delete training!");
        }

        trainingStorage.remove(key);
    }
}