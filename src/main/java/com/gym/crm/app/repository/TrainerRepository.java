package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TrainerRepository {
    List<Trainer> findAll();

    Trainer save(Trainer trainer);

    void update(Trainer trainer);

    Optional<Trainer> findById(Long id);

    void deleteById(Long id);

    //thees methods are deprecated and will be removed soon
    //todo remove methods
    @Deprecated
    Optional<Trainer> findByUsername(String username);

    @Deprecated
    void deleteByUsername(String username);
}
