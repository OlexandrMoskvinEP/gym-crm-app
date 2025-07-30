package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.exception.DataBaseErrorException;
import com.gym.crm.app.repository.TraineeRepository;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;

@RequiredArgsConstructor
@Repository
public class TraineeRepositoryImpl implements TraineeRepository {
    private static final Logger logger = LoggerFactory.getLogger(TraineeRepositoryImpl.class);

    private final TransactionExecutor txExecutor;

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
        logger.debug("Saving trainee");

        return txExecutor.performReturningWithinTx(entityManager -> {
            entityManager.persist(trainee);

            logger.debug("Trainee successfully saved");

            return trainee;
        });
    }

    @Override
    public void update(Trainee trainee) {
        logger.debug("Updating trainee");

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(trainee);
        });
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainee t WHERE t.id = :id", Trainee.class)
                        .setParameter("id", id)
                        .getResultStream()
                        .findFirst());
    }

    @Override
    public void deleteById(Long id) {
        logger.debug("Deleting trainee with id: {}", id);

        txExecutor.performWithinTx(entityManager -> {
            Trainee existing = entityManager.createQuery(
                            "SELECT t FROM Trainee t WHERE t.id = :id", Trainee.class)
                    .setParameter("id", id)
                    .getResultStream()
                    .findFirst().orElseThrow(
                            () -> new DataBaseErrorException("Cant deleteById trainee with id - " + id));

            Trainee managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainee with id: {}  - deleted", id);
        });
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Trainee t WHERE t.user.username = :username", Trainee.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst());
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
                            () -> new DataBaseErrorException("Cant deleteById trainee with username - " + username));

            Trainee managed = entityManager.contains(existing) ? existing : entityManager.merge(existing);

            entityManager.remove(managed);

            logger.debug("Trainee deleted: {}", username);
        });
    }

    @Override
    public List<Trainer> findUnassignedTrainersByTraineeUsername(String username) {
        logger.debug("Looking for unassigned trainers by trainee username: {}", username);

        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery(
                                "SELECT t FROM Trainer t " +
                                        "WHERE NOT EXISTS (" +
                                        "   SELECT 1 FROM Trainee trn " +
                                        "   JOIN trn.trainers tr " +
                                        "   WHERE trn.user.username = :username " +
                                        "   AND tr.id = t.id" +
                                        ")",
                                Trainer.class
                        )
                        .setParameter("username", username)
                        .getResultStream()
                        .toList()
        );
    }

    @Override
    public void updateTraineeTrainersById(String username, List<Long> trainerIds) {
        logger.debug("Updating trainers for trainee '{}'. New trainer IDs: {}", username, trainerIds);

        txExecutor.performWithinTx(entityManager -> {
            TypedQuery<Trainee> query = entityManager.createQuery(
                    "SELECT t FROM Trainee t JOIN FETCH t.trainers WHERE t.user.username = :username", Trainee.class);
            query.setParameter("username", username);

            Trainee trainee = query.getSingleResult();

            List<Trainer> trainers = entityManager.createQuery(
                            "SELECT tr FROM Trainer tr WHERE tr.id IN :ids", Trainer.class)
                    .setParameter("ids", trainerIds)
                    .getResultList();

            if (trainers.size() != trainerIds.size()) {
                throw new DataBaseErrorException("Some trainer IDs not found");
            }

            Set<Trainer> updated = new HashSet<>(trainers);
            Trainee toMerge = trainee.toBuilder().trainers(updated).build();
            entityManager.merge(toMerge);
        });
    }

    @Override
    public List<Trainer> updateTraineeTrainersByUsername(String traineeUsername, List<String> trainerUsernames) {
        logger.debug("Updating trainers for trainee '{}'. New trainer usernames: {}", traineeUsername, trainerUsernames);

        AtomicReference<List<Trainer>> result = new AtomicReference<>();

        txExecutor.performWithinTx(entityManager -> {
            TypedQuery<Trainee> query = entityManager.createQuery(
                    "SELECT t FROM Trainee t JOIN FETCH t.trainers WHERE t.user.username = :username", Trainee.class);
            query.setParameter("username", traineeUsername);

            Trainee trainee = query.getSingleResult();

            List<Trainer> trainers = entityManager.createQuery(
                            "SELECT tr FROM Trainer tr WHERE tr.user.username IN :usernames", Trainer.class)
                    .setParameter("usernames", trainerUsernames)
                    .getResultList();

            if (trainers.size() != trainerUsernames.size()) {
                throw new DataBaseErrorException("Some trainer usernames not found");
            }

            Set<Trainer> updated = new HashSet<>(trainers);
            Trainee updatedTrainee = trainee.toBuilder().trainers(updated).build();
            entityManager.merge(updatedTrainee);

            result.set(trainers);
        });

        return result.get();
    }
}