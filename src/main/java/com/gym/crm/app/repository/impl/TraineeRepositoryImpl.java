package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.storage.CommonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private Map<String, Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(CommonStorage commonStorage) {
        this.traineeStorage = commonStorage.getTraineeStorage();
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public Trainee save(Trainee trainee) {
        String key = trainee.getUsername();

        if (traineeStorage.containsKey(key)) {
            throw new DuplicateUsernameException("Entity already exists!");
        }
        traineeStorage.put(key, trainee);

        return trainee;
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Trainee trainee = traineeStorage.get(username);

        return Optional.ofNullable(trainee);
    }

    @Override
    public void deleteByUserName(String userName) {
        traineeStorage.remove(userName);
    }
}
