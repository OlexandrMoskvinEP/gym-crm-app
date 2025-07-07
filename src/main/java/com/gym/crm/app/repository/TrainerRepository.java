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

    //This method will be removed in subsequent MR in scope of GCA-61
    Optional<Trainer> findByUsername(String username);

    //This method will be removed in subsequent MR in scope of GCA-61
    void deleteByUsername(String username);
}
