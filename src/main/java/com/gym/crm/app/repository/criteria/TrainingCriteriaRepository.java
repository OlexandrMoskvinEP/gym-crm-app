package com.gym.crm.app.repository.criteria;

import com.gym.crm.app.domain.dto.training.TrainingFilterRequest;
import com.gym.crm.app.domain.model.Trainee;
import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.domain.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class TrainingCriteriaRepository {
    private final EntityManagerFactory entityManagerFactory;

    public List<Training> findByFilter(TrainingFilterRequest filter) {
        EntityManager em = entityManagerFactory.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Training> cq = cb.createQuery(Training.class);
        Root<Training> trainingRoot = cq.from(Training.class);

        List<Predicate> predicates = new ArrayList<>();

        Join<Training, Trainer> trainerJoin = trainingRoot.join("trainer");
        Join<Trainer, User> trainerUserJoin = trainerJoin.join("user");

        Join<Training, Trainee> traineeJoin = trainingRoot.join("trainee");
        Join<Trainee, User> traineeUserJoin = traineeJoin.join("user");

        // username condition
        if (filter.isTraineeQuery()) {
            predicates.add(cb.equal(traineeUserJoin.get("username"), filter.getUsername()));
        } else {
            predicates.add(cb.equal(trainerUserJoin.get("username"), filter.getUsername()));
        }

        // date filters
        if (filter.getFromDate() != null) {
            predicates.add(cb.greaterThanOrEqualTo(trainingRoot.get("trainingDate"), filter.getFromDate()));
        }
        if (filter.getToDate() != null) {
            predicates.add(cb.lessThanOrEqualTo(trainingRoot.get("trainingDate"), filter.getToDate()));
        }

        // partner name filter
        if (filter.getPartnerName() != null && !filter.getPartnerName().isBlank()) {
            Expression<String> fullNameExpr = cb.concat(
                    cb.concat(
                            filter.isTraineeQuery() ? trainerUserJoin.get("firstName") : traineeUserJoin.get("firstName"),
                            " "
                    ),
                    filter.isTraineeQuery() ? trainerUserJoin.get("lastName") : traineeUserJoin.get("lastName")
            );
            predicates.add(cb.like(cb.lower(fullNameExpr), "%" + filter.getPartnerName().toLowerCase() + "%"));
        }

        if (filter.getTrainingType() != null) {
            predicates.add(cb.equal(trainingRoot.get("trainingType"), filter.getTrainingType()));
        }

        cq.where(cb.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(cq).getResultList();
    }
}
