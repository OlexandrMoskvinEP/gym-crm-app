package com.gym.crm.app.config.hibernate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;
import java.util.function.Function;

public class TransactionExecutor {
    private static final Logger log = LoggerFactory.getLogger(TransactionExecutor.class);

    private final EntityManagerFactory entityManagerFactory;

    public TransactionExecutor(EntityManagerFactory entityManagerFactory) {
        this.entityManagerFactory = entityManagerFactory;
    }

    public void performWithinTx(Consumer<EntityManager> action) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            log.debug("Transaction started");
            action.accept(entityManager);

            transaction.commit();
        } catch (Exception ex) {
            if (transaction.isActive()) {
                log.debug("Transaction failed");

                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }


    public <T> T performReturningWithinTx(Function<EntityManager, T> action) {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            log.debug("Transaction with returning started");
            T result = action.apply(entityManager);
            transaction.commit();

            return result;

        } catch (Exception ex) {
            if (transaction.isActive()) {
                log.debug("Transaction with returning failed");

                transaction.rollback();
            }
            throw ex;
        } finally {
            entityManager.close();
        }
    }
}
