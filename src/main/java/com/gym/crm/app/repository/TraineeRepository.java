package com.gym.crm.app.repository;

import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByUserUsername(String username);

    @Transactional
    void deleteByUserUsername(String userName);

    @Query("""
                SELECT t FROM Trainer t
                WHERE NOT EXISTS (
                    SELECT 1 FROM Trainee trn
                    JOIN trn.trainers tr
                    WHERE trn.user.username = :username
                    AND tr.id = t.id
                )
            """)
    List<Trainer> findUnassignedTrainersByTraineeUsername(@Param("username") String username);
}
