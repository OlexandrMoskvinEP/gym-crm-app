package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.AlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private Map<String, Trainee> storage;

    @Autowired
    public void setTrainerStorage(Map<String, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Trainee save(Trainee trainee) {
        String key = trainee.getUsername();

        if (storage.containsKey(key)) {
            throw new AlreadyExistException("Entity already exists!");
        }
        storage.put(key, trainee);

        return trainee;
    }

    @Override
    public Optional<Trainee> findByUserName(String userName) {
        Trainee trainee = storage.get(userName);

        return Optional.ofNullable(trainee);
    }

    @Override
    public void deleteByUserName(String userName) {
        storage.remove(userName);
    }
}
