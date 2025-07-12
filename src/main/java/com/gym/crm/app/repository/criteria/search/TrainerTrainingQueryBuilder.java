package com.gym.crm.app.repository.criteria.search;

import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.repository.criteria.search.filters.TrainerTrainingSearchFilter;
import com.gym.crm.app.repository.criteria.search.filters.TrainingSearchFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TrainerTrainingQueryBuilder extends TrainingQueryBuilder {

    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root,
                                         TrainingSearchFilter baseCriteria, List<Predicate> predicates) {
        TrainerTrainingSearchFilter criteria = (TrainerTrainingSearchFilter) baseCriteria;
        addTraineeFullNamePredicate(cb, root, criteria, predicates);
    }

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username) {
        return cb.equal(root.get("trainer").get("user").get("username"), username);
    }

    private void addTraineeFullNamePredicate(CriteriaBuilder cb, Root<Training> root,
                                             TrainerTrainingSearchFilter criteria, List<Predicate> predicates) {
        if (criteria.getTraineeName() == null || criteria.getTraineeName().isBlank()) return;

        var fullNameExpr = cb.concat(
                root.get("trainee").get("user").get("firstName"),
                cb.concat(" ", root.get("trainee").get("user").get("lastName"))
        );

        predicates.add(cb.like(cb.lower(fullNameExpr), "%" + criteria.getTraineeName().toLowerCase() + "%"));
    }
}
