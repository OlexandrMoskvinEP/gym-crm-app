package com.gym.crm.app.repository.criteria.search;

import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.repository.criteria.search.filters.TraineeTrainingSearchFilter;
import com.gym.crm.app.repository.criteria.search.filters.TrainingSearchFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TraineeTrainingQueryBuilder extends TrainingQueryBuilder {
    @Override
    protected void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root,
                                         TrainingSearchFilter baseCriteria, List<Predicate> predicates) {
        var criteria = (TraineeTrainingSearchFilter) baseCriteria;

        addTrainerFullNamePredicate(cb, root, criteria, predicates);
        addTrainingTypePredicate(cb, root, criteria, predicates);
    }

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username) {
        return cb.equal(root.get("trainee").get("user").get("username"), username);
    }

    private void addTrainerFullNamePredicate(CriteriaBuilder cb, Root<Training> root,
                                             TraineeTrainingSearchFilter criteria, List<Predicate> predicates) {
        addFullNameLikePredicate(cb, root, predicates,
                criteria.getTrainerFullName(), "trainer.user");
    }

    private void addTrainingTypePredicate(CriteriaBuilder cb, Root<Training> root,
                                          TraineeTrainingSearchFilter criteria, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getTrainingTypeName())
                .filter(s -> !s.isBlank())
                .ifPresent(typeName ->
                        predicates.add(cb.equal(root.get("trainingType").get("trainingTypeName"), typeName)));
    }
}
