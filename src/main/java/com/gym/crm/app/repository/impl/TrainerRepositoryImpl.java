package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TrainerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    private TransactionExecutor txExecutor;

    @Autowired
    public void setTxExecutor(TransactionExecutor txExecutor) {
        this.txExecutor = txExecutor;
    }

    @Override
    public List<Trainer> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainer t ", Trainer.class)
                        .getResultStream()
                        .toList()
        );
    }

    @Override
    public Trainer save(Trainer trainer) {
        logger.debug("Saving trainee: {}", trainer.getUser().getUsername());

        return txExecutor.performReturningWithinTx(entityManager -> {
            entityManager.persist(trainer);

            logger.debug("Trainee: {} successfully saved", trainer.getUser().getUsername());

            return trainer;
        });
    }

    @Override
    public void update(Trainer trainer) {
        logger.debug("Updating trainee: {}", trainer.getUser().getUsername());

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(trainer);
        });
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public void deleteByUserName(String username) {
        logger.debug("Deleting trainer with username: {}", username);

        txExecutor.performWithinTx(entityManager -> {
            Trainer existing = entityManager.createQuery(
                            "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new EntityNotFoundException("Cant delete trainer with username - " + username));

            Trainer managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainer deleted: {}", username);
        });

    }
}
