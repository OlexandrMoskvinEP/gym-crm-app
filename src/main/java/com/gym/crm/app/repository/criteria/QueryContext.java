package com.gym.crm.app.repository.criteria;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class QueryContext <T>{
    public final EntityManager em;
    public final CriteriaBuilder cb;
    public final CriteriaQuery<T> cq;
    public final Root<T> root;
    public final List<Predicate> predicates = new ArrayList<>();

    public QueryContext(EntityManager em, CriteriaBuilder cb, CriteriaQuery<T> cq, Root<T> root) {
        this.em = em;
        this.cb = cb;
        this.cq = cq;
        this.root = root;
    }

    public static <T> QueryContext<T> of(Class<T> entityClass, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        Root<T> root = cq.from(entityClass);
        return new QueryContext<>(em, cb, cq, root);
    }

    public TypedQuery<T> buildQuery() {
        cq.where(cb.and(predicates.toArray(new Predicate[0])));
        return em.createQuery(cq);
    }
}
