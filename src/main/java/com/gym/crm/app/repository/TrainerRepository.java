package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    List<Trainer> findAll();

    Trainer save(Trainer trainer);

    void update(Trainer trainer);

    Optional<Trainer> findByUsername(String username);

    void deleteByUserName(String username);
}
