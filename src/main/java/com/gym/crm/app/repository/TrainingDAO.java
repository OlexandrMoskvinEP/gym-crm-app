package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class TrainingDAO implements TrainingRepository {
    private JsonStorageHandler storage;

    @Autowired
    public void setStorage(JsonStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.getTrainingStorage().values());
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
    public List<Training> findByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        return findAll().stream()
                .filter(t -> t.getTrainerId() == trainerId)
                .filter(t -> t.getTraineeId() == traineeId)
                .filter(t -> t.getTrainingDate().equals(date))
                .collect(Collectors.toList());
    }

    @Override
    public void save(Training training) {
        if (!findByTrainerAndTraineeAndDate(training.getTrainerId(), training.getTraineeId(), training.getTrainingDate()).isEmpty()) {
            throw new RuntimeException("Entity already exists!");
        }
        String key = training.getTrainerId() + training.getTraineeId() + training.getTrainingDate().toString();
        storage.getTrainingStorage().put(key, training);
    }

    @Override
    public void deleteByTrainerAndTraineeAndDate(int trainerId, int traineeId, LocalDate date) {
        String key = trainerId + traineeId + date.toString();
        storage.getTrainingStorage().remove(key);
    }
}
