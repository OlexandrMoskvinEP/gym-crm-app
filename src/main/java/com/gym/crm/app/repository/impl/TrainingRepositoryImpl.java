package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.DuplicateEntityException;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingRepository;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingRepositoryImpl implements TrainingRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainingRepositoryImpl.class);

    private final TransactionExecutor txExecutor;

    public TrainingRepositoryImpl(EntityManagerFactory managerFactory) {
        this.txExecutor = new TransactionExecutor(managerFactory);
    }

    @Override
    public List<Training> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Training t ", Training.class)
                        .getResultStream()
                        .toList()
        );
    }

    @Override
    public Optional<Training> findById(Long id) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Training t WHERE t.id = :id ", Training.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public Training save(Training training) {
        logger.debug("Saving training: {}", training.getTrainingName());

        return txExecutor.performReturningWithinTx(entityManager -> {
            boolean exists = !entityManager.createQuery("""
                            SELECT t.id FROM Training t 
                            WHERE t.trainingDate = :date 
                              AND t.trainer.id = :trainerId 
                              AND t.trainee.id = :traineeId
                            """, Long.class)
                    .setParameter("date", training.getTrainingDate())
                    .setParameter("trainerId", training.getTrainer().getId())
                    .setParameter("traineeId", training.getTrainee().getId())
                    .getResultList()
                    .isEmpty();

            if (exists) {
                throw new DuplicateEntityException("Such training already exists");
            }

            entityManager.persist(training);
            logger.debug("Training: {} successfully saved", training.getTrainingName());
            return training;
        });
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting trainer with id: {}", id);

        txExecutor.performWithinTx(entityManager -> {
            Training existing = entityManager.createQuery(
                            "SELECT t FROM Training t WHERE t.id = :id", Training.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new EntityNotFoundException("Cant deleteById training with id - " + id));

            Training managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Training deleted: {}", managed.getTrainingName());
        });
    }
}