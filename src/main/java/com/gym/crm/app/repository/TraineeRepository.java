package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    List<Trainee> findAll();

    Trainee save(Trainee trainee);

    void update(Trainee trainee);

    Optional<Trainee> findByUsername(String username);

    void deleteByUsername(String userName);
}
