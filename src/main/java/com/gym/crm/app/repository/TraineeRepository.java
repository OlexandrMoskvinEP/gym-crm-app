package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    List<Trainee> findAll();

    Trainee save(Trainee trainee);

    void update(Trainee trainee);

    Optional<Trainee> findById(Long id);

    void deleteById(Long id);

    //todo remove methods
    //thees methods are deprecated and will be removed soon
    @Deprecated
    Optional<Trainee> findByUsername(String username);
    @Deprecated
    void deleteByUsername(String userName);
}
