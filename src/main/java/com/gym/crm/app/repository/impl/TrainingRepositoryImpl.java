package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.exception.DuplicateEntityException;
import com.gym.crm.app.repository.TrainingRepository;
import com.gym.crm.app.repository.criteria.search.TraineeTrainingQueryBuilder;
import com.gym.crm.app.repository.criteria.search.TrainerTrainingQueryBuilder;
import com.gym.crm.app.repository.criteria.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.criteria.search.filters.TrainerTrainingSearchFilter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingRepositoryImpl implements TrainingRepository {
    private static final Logger logger = LoggerFactory.getLogger(TrainingRepositoryImpl.class);

    private final TransactionExecutor txExecutor;
    private final TrainerTrainingQueryBuilder trainerQueryBuilder;
    private final TraineeTrainingQueryBuilder traineeQueryBuilder;

    @Override
    public List<Training> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT t FROM Training t ", Training.class)
                        .getResultStream()
                        .toList()
        );
    }

    @Override
    public void update(Training training) {
        logger.debug("Updating training");

        txExecutor.performWithinTx(entityManager -> {
            entityManager.merge(training);
        });
    }

    @Override
    public Training save(Training training) {
        logger.debug("Saving training: {}", training.getTrainingName());

        return txExecutor.performReturningWithinTx(entityManager -> {
            boolean isExist = !entityManager.createQuery("""
                             SELECT t.id FROM Training t\s
                             WHERE t.trainingDate = :date\s
                               AND t.trainer.id = :trainerId\s
                               AND t.trainee.id = :traineeId
                            \s""", Long.class)
                    .setParameter("date", training.getTrainingDate())
                    .setParameter("trainerId", training.getTrainer().getId())
                    .setParameter("traineeId", training.getTrainee().getId())
                    .getResultList()
                    .isEmpty();

            if (isExist) {
                throw new DuplicateEntityException("Such training already exists");
            }

            entityManager.persist(training);
            logger.debug("Training: {} successfully saved", training.getTrainingName());
            return training;
        });
    }

    @Override
    public List<Training> findByTrainerCriteria(TrainerTrainingSearchFilter filter) {
        return txExecutor.performReturningWithinTx(entityManager -> {
            var session = entityManager.unwrap(org.hibernate.Session.class);
            var cb = session.getCriteriaBuilder();
            var query = trainerQueryBuilder.build(cb, filter);

            return session.createQuery(query).getResultList();
        });
    }

    @Override
    public List<Training> findByTraineeCriteria(TraineeTrainingSearchFilter filter) {
        return txExecutor.performReturningWithinTx(entityManager -> {
            var session = entityManager.unwrap(org.hibernate.Session.class);
            var cb = session.getCriteriaBuilder();
            var query = traineeQueryBuilder.build(cb, filter);

            return session.createQuery(query).getResultList();
        });
    }
}