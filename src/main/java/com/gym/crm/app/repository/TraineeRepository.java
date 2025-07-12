package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;

import java.util.List;
import java.util.Optional;

public interface TraineeRepository {

    List<Trainee> findAll();

    Trainee save(Trainee trainee);

    void update(Trainee trainee);

    Optional<Trainee> findById(Long id);

    void deleteById(Long id);

    Optional<Trainee> findByUsername(String username);

    void deleteByUsername(String userName);

    List<Trainer> findUnassignedTrainersByTraineeUsername(String username);

    void updateTraineeTrainers(String username, List<Long> trainerIds);
}
