package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.storage.CommonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
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
            throw new EntityNotFoundException("Failed while deleting trainer!");
        }

        trainerStorage.remove(username);
    }
}
