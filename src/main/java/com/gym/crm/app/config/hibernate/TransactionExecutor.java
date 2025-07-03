package com.gym.crm.app.config.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;
import java.util.function.Function;

@Configuration
public class TransactionExecutor {
    @Autowired
    private EntityManagerFactory entityManagerFactory;

    public void performWithinTx(Consumer<EntityManager> action) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            action.accept(em);
            tx.commit();
        } catch (Exception ex) {
            tx.rollback();
            throw ex;
        } finally {
            em.close();
        }
    }

    public <T> T performReturningWithinTx(Function<EntityManager, T> action) {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = null;

        try (em) {
            tx = em.getTransaction();
            tx.begin();
            T result = action.apply(em);
            tx.commit();

            return result;

        } catch (Exception ex) {
            if (tx != null) {
                tx.rollback();
            }
            throw ex;
        }
    }
}
