package com.gym.crm.app.aspect.tx;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionAspectTest {
    private final EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
    private final EntityManager entityManager = mock(EntityManager.class);
    private final EntityTransaction transaction = mock(EntityTransaction.class);
    private final ProceedingJoinPoint pjp = mock(ProceedingJoinPoint.class);

    private final TransactionAspect aspect = new TransactionAspect(entityManagerFactory);

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(transaction);
    }

    @Test
    void shouldBeginAndCommitTransaction() throws Throwable {
        CoreTransactional annotation = mock(CoreTransactional.class);
        Object result;

        when(pjp.proceed()).thenReturn("OK");

        result = aspect.wrapInTransaction(pjp, annotation);

        assertEquals("OK", result);
        verify(transaction).begin();
        verify(transaction).commit();
        verify(entityManager).close();
    }

    @Test
    void shouldRollbackOnException() throws Throwable {
        CoreTransactional annotation = mock(CoreTransactional.class);

        when(pjp.proceed()).thenThrow(new RuntimeException("fail"));
        when(transaction.isActive()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> aspect.wrapInTransaction(pjp, annotation));

        verify(transaction).begin();
        verify(transaction).rollback();
        verify(entityManager).close();
    }
}