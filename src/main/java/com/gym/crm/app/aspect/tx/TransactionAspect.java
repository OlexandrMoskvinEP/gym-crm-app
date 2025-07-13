package com.gym.crm.app.aspect.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class TransactionAspect {

    private final EntityManagerFactory entityManagerFactory;

    @Around("@annotation(coreTx)")
    public Object wrapInTransaction(ProceedingJoinPoint pjp, CoreTransactional coreTx) throws Throwable {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = null;

        try (em) {
            tx = em.getTransaction();
            tx.begin();

            Object result = pjp.proceed();

            tx.commit();

            return result;
        } catch (Throwable ex) {
            assert tx != null;

            if (tx.isActive()) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
