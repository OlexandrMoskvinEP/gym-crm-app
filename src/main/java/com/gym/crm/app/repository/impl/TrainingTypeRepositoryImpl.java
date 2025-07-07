package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainingTypeRepository;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainingTypeRepositoryImpl implements TrainingTypeRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainingTypeRepositoryImpl.class);

    private final TransactionExecutor txExecutor;

    public TrainingTypeRepositoryImpl(EntityManagerFactory managerFactory) {
        this.txExecutor = new TransactionExecutor(managerFactory);
    }

    @Override
    public TrainingType save(TrainingType trainingType) {
        logger.debug("Saving training type: {}", trainingType.getTrainingTypeName());

        return txExecutor.performReturningWithinTx(entityManager -> {
            entityManager.persist(trainingType);

            logger.debug("Training type - {} successfully saved", trainingType.getTrainingTypeName());

            return trainingType;
        });
    }

    @Override
    public Optional<TrainingType> findById(Long id) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM TrainingType t WHERE t.id = :id ", TrainingType.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public void update(TrainingType trainingType) {
        logger.debug("Updating training type");

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(trainingType);
        });
    }

    @Override
    public List<TrainingType> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM TrainingType t ", TrainingType.class)
                        .getResultStream().toList());
    }

    @Override
    public void deleteById(Long id) {
        txExecutor.performWithinTx(entityManager -> {
            TrainingType existing = entityManager.createQuery("SELECT t FROM TrainingType t WHERE t.id = :id", TrainingType.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new EntityNotFoundException("Cant deleteById training typr with id - " + id));


            TrainingType managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("TrainingType deleted: {}", managed.getTrainingTypeName());
        });
    }
}
