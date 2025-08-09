package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Optional<Trainer> findByUserUsername(String username);

    @Transactional
    void deleteByUserUsername(String username);

    List<Trainer> findByUserUsernameIn(List<String> usernames);
}
