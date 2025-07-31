package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.repository.TrainerRepository;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class TrainerRepositoryImpl implements TrainerRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainerRepositoryImpl.class);

    private final TransactionExecutor txExecutor;

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
        logger.debug("Saving trainer");

        return txExecutor.performReturningWithinTx(entityManager -> {
            entityManager.persist(trainer);

            logger.debug("Trainer successfully saved");

            return trainer;
        });
    }

    @Override
    public void update(Trainer trainer) {
        logger.debug("Updating trainer");

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(trainer);
        });
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainer t WHERE t.id = :id", Trainer.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting trainer with id: {}", id);

        txExecutor.performWithinTx(entityManager -> {
            Trainer existing = entityManager.createQuery(
                            "SELECT t FROM Trainer t WHERE t.id = :id", Trainer.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new DataBaseErrorException("Cant deleteById trainer with id - " + id));

            Trainer managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainer with id : {} deleted", id);
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
    public void deleteByUsername(String username) {
        logger.debug("Deleting trainer with username: {}", username);

        txExecutor.performWithinTx(entityManager -> {
            Trainer existing = entityManager.createQuery(
                            "SELECT t FROM Trainer t WHERE t.user.username = :username", Trainer.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new DataBaseErrorException("Cant deleteById trainer with username - " + username));

            Trainer managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainer deleted: {}", username);
        });
    }

    @Override
    public List<Trainer> findByUserUsernameIn(List<String> usernames) {
        return List.of();
    }
}
