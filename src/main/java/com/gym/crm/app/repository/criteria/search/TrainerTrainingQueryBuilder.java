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
        var criteria = (TrainerTrainingSearchFilter) baseCriteria;

        addTraineeFullNamePredicate(cb, root, criteria, predicates);
    }

    @Override
    protected Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username) {
        return cb.equal(root.get("trainer").get("user").get("username"), username);
    }

    private void addTraineeFullNamePredicate(CriteriaBuilder cb, Root<Training> root,
                                             TrainerTrainingSearchFilter criteria, List<Predicate> predicates) {
        addFullNameLikePredicate(cb, root, predicates,
                criteria.getTraineeFullName(), "trainee.user");
    }
}