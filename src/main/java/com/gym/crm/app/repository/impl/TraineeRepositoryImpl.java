package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.TraineeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger logger = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    private TransactionExecutor txExecutor;

    @Autowired
    public void setTxExecutor(TransactionExecutor txExecutor) {
        this.txExecutor = txExecutor;
    }

    @Override
    public List<Trainee> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainee t ", Trainee.class)
                        .getResultStream()
                        .toList()
        );
    }

    @Override
    public Trainee save(Trainee trainee) {
        logger.debug("Saving trainee: {}", trainee.getUser().getUsername());

        return txExecutor.performReturningWithinTx(entityManager -> {
            entityManager.persist(trainee);

            logger.debug("Trainee: {} successfully saved", trainee.getUser().getUsername());

            return trainee;
        });
    }

    @Override
    public void update(Trainee trainee) {
        logger.debug("Updating trainee: {}", trainee.getUser().getUsername());

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(trainee);
        });
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return txExecutor.performReturningWithinTx(em ->
                em.createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public void deleteByUsername(String username) {
        logger.debug("Deleting trainee with username: {}", username);

        txExecutor.performWithinTx(entityManager -> {
            Trainee existing = entityManager.createQuery(
                            "SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new EntityNotFoundException("Cant delete trainee with username - " + username));

            Trainee managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainee deleted: {}", username);
        });
    }
}
