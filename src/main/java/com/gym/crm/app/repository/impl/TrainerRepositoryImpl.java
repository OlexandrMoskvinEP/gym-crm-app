package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.DuplicateUsernameException;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.storage.CommonStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private Map<String, Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(Map<String, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public Trainer save(Trainer trainer) {
        String key = trainer.getUsername();

        if (trainerStorage.containsKey(key)) {
            throw new DuplicateUsernameException("Entity already exists!");
        }
        trainerStorage.put(key, trainer);

        return trainer;
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        Trainer trainer = trainerStorage.get(userName);

        return Optional.ofNullable(trainer);
    }

    @Override
    public void deleteByUserName(String userName) {
        trainerStorage.remove(userName);
    }
}
