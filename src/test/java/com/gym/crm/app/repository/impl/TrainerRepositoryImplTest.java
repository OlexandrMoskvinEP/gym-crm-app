package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.stream.Stream;

import static com.gym.crm.app.data.maker.TrainerMaker.constructTrainer;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainerRepositoryImplTest {
    @Mock
    EntityManagerFactory entityManagerFactory;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction tx;

    @InjectMocks
    TrainerRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(tx);
        repository = new TrainerRepositoryImpl(entityManagerFactory);
    }

    @Test
    void shouldSaveAndFindTrainer() {
        Trainer trainer = constructTrainer();

        doNothing().when(entityManager).persist(trainer);

        TypedQuery<Trainer> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(trainer));

        repository.save(trainer);

        Optional<Trainer> found = repository.findById(2L);

        verify(entityManager).persist(trainer);
        assertTrue(found.isPresent());
    }

    @Test
    void shouldUpdateTrainer() {
        Trainer trainer = constructTrainer();

        when(entityManager.merge(trainer)).thenReturn(trainer);

        repository.update(trainer);

        verify(entityManager).merge(trainer);
    }

    @Test
    void shouldDeleteByIdById() {
        Trainer trainer = constructTrainer();

        TypedQuery<Trainer> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Trainer.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(trainer));

        when(entityManager.contains(trainer)).thenReturn(false);
        when(entityManager.merge(trainer)).thenReturn(trainer);

        doNothing().when(entityManager).remove(trainer);

        repository.deleteById(5L);

        verify(entityManager).remove(trainer);
    }
}