package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TrainerDAO implements TrainerRepository {
    private JsonStorageHandler storage;

    @Autowired
    public void setStorage(JsonStorageHandler storage) {
        this.storage = storage;
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.getTrainerStorage().values());
    }

    @Override
    public Trainer save(Trainer trainer) {
        String key = trainer.getUsername();
        if (storage.getTrainerStorage().containsKey(key)) {
            throw new RuntimeException("Entity already exists!");
        }
        storage.getTrainerStorage().put(key, trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        Trainer trainer = storage.getTrainerStorage().get(userName);
        return Optional.ofNullable(trainer);
    }

    @Override
    public void deleteByUserName(String userName) {
        storage.getTrainerStorage().remove(userName);
    }
}
