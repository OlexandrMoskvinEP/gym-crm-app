package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainer;
import com.gym.crm.app.domain.model.Training;
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

import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;

import static com.gym.crm.app.data.maker.TrainerMaker.constructTrainer;
import static com.gym.crm.app.data.maker.TrainingMaker.constructTraining;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingRepositoryImplTest {
    @Mock
    EntityManagerFactory entityManagerFactory;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction tx;

    @InjectMocks
    TrainingRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(tx);
        repository = new TrainingRepositoryImpl(entityManagerFactory);
    }

    @Test
    void shouldSaveAndFindTraining() {
        Training training = constructTraining();

        doNothing().when(entityManager).persist(training);

        TypedQuery<Long> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Long.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.getResultList()).thenReturn(Collections.emptyList());

        TypedQuery<Training> mockTrainingQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(mockTrainingQuery);
        when(mockTrainingQuery.setParameter(eq("id"), any())).thenReturn(mockTrainingQuery);
        when(mockTrainingQuery.getResultStream()).thenReturn(Stream.of(training));

        repository.save(training);

        Optional<Training> found = repository.findById(897L);

        verify(entityManager).persist(training);
        assertTrue(found.isPresent());
    }

    @Test
    void shouldUpdateTraining() {
        Training training = constructTraining();

        when(entityManager.merge(training)).thenReturn(training);

        repository.update(training);

        verify(entityManager).merge(training);
    }

    @Test
    void shouldDeleteById() {
        Training training = constructTraining();

        TypedQuery<Training> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Training.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(training));

        when(entityManager.contains(training)).thenReturn(false);
        when(entityManager.merge(training)).thenReturn(training);

        doNothing().when(entityManager).remove(training);

        repository.deleteById(6L);

        verify(entityManager).remove(training);
    }
}