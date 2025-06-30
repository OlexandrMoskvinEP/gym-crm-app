package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.storage.CommonStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger logger = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    private Map<String, Trainee> traineeStorage;
    private final AtomicInteger traineeCounter = new AtomicInteger(1);

    @Autowired
    public void setTraineeStorage(CommonStorage commonStorage) {
        this.traineeStorage = commonStorage.getTraineeStorage();
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public Trainee saveTrainee(Trainee trainee) {
        String key = trainee.getUsername();

        if (traineeStorage.containsKey(key)) {
           throw new DuplicateUsernameException("Entity already exists!");
        }

        Trainee traineeWithId = trainee.toBuilder()
                .userId(traineeCounter.getAndIncrement())
                .build();

        traineeStorage.put(key, traineeWithId);

        return traineeWithId;
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        Trainee trainee = traineeStorage.get(username);

        return Optional.ofNullable(trainee);
    }

    @Override
    public void deleteByUserName(String username) {
        if (!traineeStorage.containsKey(username)) {
            throw new EntityNotFoundException("Failed while deleting trainer!");
        }

        traineeStorage.remove(username);
    }
}
