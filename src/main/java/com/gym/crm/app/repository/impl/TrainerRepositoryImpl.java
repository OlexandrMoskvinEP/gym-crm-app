package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
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
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    private Map<String, Trainer> trainerStorage;
    private final AtomicInteger trainerCounter = new AtomicInteger(1);

    @Autowired
    public void setTrainerStorage(CommonStorage commonStorage) {
        this.trainerStorage = commonStorage.getTrainerStorage();
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public Trainer saveTrainer(Trainer trainer) {
        String key = trainer.getUsername();

        if (trainerStorage.containsKey(key)) {
            logger.warn("Trainer with username {} already exists", trainer.getUsername());

            throw new DuplicateUsernameException("Entity already exists!");
        }

        Trainer trainerWithId = trainer.toBuilder()
                .userId(trainerCounter.getAndIncrement())
                .build();

        trainerStorage.put(key, trainerWithId);

        return trainerWithId;
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        Trainer trainer = trainerStorage.get(username);

        return Optional.ofNullable(trainer);
    }

    @Override
    public void deleteByUserName(String username) {
        if (!trainerStorage.containsKey(username)) {
            logger.warn("Trainer with username {} does`t exist", username);

            throw new EntityNotFoundException("Failed while deleting trainer!");
        }

        trainerStorage.remove(username);
    }
}
