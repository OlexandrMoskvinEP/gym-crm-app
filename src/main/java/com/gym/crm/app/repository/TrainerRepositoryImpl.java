package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.AlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private Map<String, Trainer> storage;

    @Autowired
    public void setTrainerStorage(Map<String, Trainer> trainerStorage) {
        this.storage = trainerStorage;
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public Trainer save(Trainer trainer) {
        String key = trainer.getUsername();

        if (storage.containsKey(key)) {
            throw new AlreadyExistException("Entity already exists!");
        }
        storage.put(key, trainer);

        return trainer;
    }

    @Override
    public Optional<Trainer> findByUserName(String userName) {
        Trainer trainer = storage.get(userName);

        return Optional.ofNullable(trainer);
    }

    @Override
    public void deleteByUserName(String userName) {
        storage.remove(userName);
    }
}
