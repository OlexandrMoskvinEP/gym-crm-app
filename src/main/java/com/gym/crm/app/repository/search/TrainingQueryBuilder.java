package com.gym.crm.app.repository.search;

import com.gym.crm.app.domain.model.Training;
import com.gym.crm.app.repository.search.filters.TrainingSearchFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class TrainingQueryBuilder {
    public CriteriaQuery<Training> build(CriteriaBuilder cb, TrainingSearchFilter criteria) {
        CriteriaQuery<Training> query = cb.createQuery(Training.class);
        Root<Training> root = query.from(Training.class);
        List<Predicate> predicates = new ArrayList<>();

        addUsernamePredicate(cb, root, criteria, predicates);
        addDateRange(cb, root, criteria, predicates);
        addSpecificPredicates(cb, root, criteria, predicates);

        query.select(root).where(cb.and(predicates.toArray(new Predicate[0])));

        return query;
    }

    protected abstract void addSpecificPredicates(CriteriaBuilder cb, Root<Training> root,
                                                  TrainingSearchFilter criteria, List<Predicate> predicates);

    protected abstract Predicate getUsernamePredicate(CriteriaBuilder cb, Root<Training> root, String username);

    protected void addUsernamePredicate(CriteriaBuilder cb, Root<Training> root,
                                        TrainingSearchFilter criteria, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getUsername())
                .filter(s -> !s.isBlank())
                .ifPresent(username -> predicates.add(getUsernamePredicate(cb, root, username)));
    }

    protected void addDateRange(CriteriaBuilder cb, Root<Training> root,
                                TrainingSearchFilter criteria, List<Predicate> predicates) {
        Optional.ofNullable(criteria.getFromDate())
                .ifPresent(from -> predicates.add(cb.greaterThanOrEqualTo(root.get("trainingDate"), from)));
        Optional.ofNullable(criteria.getToDate())
                .ifPresent(to -> predicates.add(cb.lessThanOrEqualTo(root.get("trainingDate"), to)));
    }

    protected void addFullNameLikePredicate(CriteriaBuilder cb,
                                            Root<Training> root,
                                            List<Predicate> predicates,
                                            String fullName,
                                            String userPathPrefix) {
        Optional.ofNullable(fullName)
                .filter(s -> !s.isBlank())
                .ifPresent(value -> {
                    Join<?, ?> userJoin = resolveJoinPath(root, userPathPrefix);

                    Expression<String> fullNameExpr = cb.concat(
                            cb.concat(userJoin.get("firstName"), " "),
                            userJoin.get("lastName")
                    );

                    predicates.add(cb.like(cb.lower(fullNameExpr), "%" + value.toLowerCase() + "%"));
                });
    }

    private Join<?, ?> resolveJoinPath(Root<Training> root, String path) {
        String[] parts = path.split("\\.");
        Join<?, ?> join = root.join(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            join = join.join(parts[i]);
        }

        return join;
    }
}