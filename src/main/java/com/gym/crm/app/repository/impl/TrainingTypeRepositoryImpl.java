package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.TrainingType;
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
    public Optional<TrainingType> findByName(String trainingTypeName) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery(
                                "SELECT t FROM TrainingType t WHERE t.trainingTypeName = :name", TrainingType.class)
                        .setParameter("name", trainingTypeName)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public List<TrainingType> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM TrainingType t ", TrainingType.class)
                        .getResultStream().toList());
    }
}
