package com.gym.crm.app.aspect.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
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

            log.debug("Begin transaction: {}", pjp.getSignature());
            Object result = pjp.proceed();

            tx.commit();
            log.debug("Commit transaction: {}", pjp.getSignature());

            return result;
        } catch (Throwable ex) {
            assert tx != null;

            if (tx.isActive()) {
                log.error("Rollback transaction: {}", pjp.getSignature(), ex);
                tx.rollback();
            }
            throw ex;
        }
    }
}
