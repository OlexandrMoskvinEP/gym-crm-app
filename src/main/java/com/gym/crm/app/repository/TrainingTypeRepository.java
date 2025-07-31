package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.TrainingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TrainingTypeRepository extends JpaRepository<TrainingType, Long> {
   @Query
    Optional<TrainingType> findByTrainingTypeName( String trainingTypeName);
}
