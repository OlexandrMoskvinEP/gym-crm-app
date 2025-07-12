package com.gym.crm.app.repository.criteria;

import com.gym.crm.app.domain.dto.training.TrainingFilterRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingCriteriaRepository {
    private final EntityManagerFactory entityManagerFactory;

    public List<Training> findByFilter(TrainingFilterRequest filter) {
        EntityManager em = entityManagerFactory.createEntityManager();

        QueryContext<Training> ctx = QueryContext.of(Training.class, em);

        Join<Training, Trainer> trainerJoin = ctx.root.join("trainer");
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");

        Join<Training, Trainee> traineeJoin = ctx.root.join("trainee");
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

        if (filter.isTraineeQuery()) {
            ctx.predicates.add(ctx.cb.equal(traineeUserJoin.get("username"), filter.getUsername()));
        } else {
            ctx.predicates.add(ctx.cb.equal(trainerUserJoin.get("username"), filter.getUsername()));
        }

        if (filter.getFromDate() != null) {
            ctx.predicates.add(ctx.cb.greaterThanOrEqualTo(ctx.root.get("trainingDate"), filter.getFromDate()));
        }

        if (filter.getToDate() != null) {
            ctx.predicates.add(ctx.cb.lessThanOrEqualTo(ctx.root.get("trainingDate"), filter.getToDate()));
        }

        if (filter.getPartnerName() != null && !filter.getPartnerName().isBlank()) {
            Expression<String> fullNameExpr = ctx.cb.concat(
                    ctx.cb.concat(
                            filter.isTraineeQuery() ? trainerUserJoin.get("firstName") : traineeUserJoin.get("firstName"),
                            " "
                    ),
                    filter.isTraineeQuery() ? trainerUserJoin.get("lastName") : traineeUserJoin.get("lastName")
            );
            ctx.predicates.add(ctx.cb.like(ctx.cb.lower(fullNameExpr), "%" + filter.getPartnerName().toLowerCase() + "%"));
        }

        if (filter.getTrainingType() != null) {
            ctx.predicates.add(ctx.cb.equal(ctx.root.get("trainingType"), filter.getTrainingType()));
        }

        return ctx.buildQuery().getResultList();
    }
}
