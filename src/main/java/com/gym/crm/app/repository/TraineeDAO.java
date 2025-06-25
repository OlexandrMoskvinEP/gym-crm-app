package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TraineeDAO implements TraineeRepository {
    private JsonStorageHandler storage;

    @Autowired
    public void setStorage(JsonStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.getTraineeStorage().values());
    }

    @Override
    public Trainee save(Trainee trainee) {
        String key = trainee.getUsername();
        if (storage.getTraineeStorage().containsKey(key)) {
            throw new RuntimeException("Entity already exists!");
        }
        storage.getTraineeStorage().put(key, trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findByUserName(String userName) {
        Trainee trainee = storage.getTraineeStorage().get(userName);
        return Optional.ofNullable(trainee);
    }

    @Override
    public void deleteByUserName(String userName) {
        storage.getTraineeStorage().remove(userName);
    }
}
