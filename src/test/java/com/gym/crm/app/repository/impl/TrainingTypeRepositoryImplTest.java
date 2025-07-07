package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.TrainingType;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeRepositoryImplTest {
    @Mock
    EntityManagerFactory entityManagerFactory;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction tx;

    @InjectMocks
    TrainingTypeRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(tx);
        repository = new TrainingTypeRepositoryImpl(entityManagerFactory);
    }

    @Test
    void shouldSaveAndFindTrainingType() {
        TrainingType type = constructTrainingType();

        doNothing().when(entityManager).persist(type);

        TypedQuery<TrainingType> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(type));

        repository.save(type);

        Optional<TrainingType> found = repository.findById(3L);

        verify(entityManager).persist(type);
        assertTrue(found.isPresent());
    }

    @Test
    void shouldUpdateTrainer() {
        TrainingType type = constructTrainingType();

        when(entityManager.merge(type)).thenReturn(type);

        repository.update(type);

        verify(entityManager).merge(type);
    }

    @Test
    void shouldDeleteByIdById() {
        TrainingType type = constructTrainingType();

        TypedQuery<TrainingType> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(TrainingType.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(type));

        when(entityManager.contains(type)).thenReturn(false);
        when(entityManager.merge(type)).thenReturn(type);

        doNothing().when(entityManager).remove(type);

        repository.deleteById(2L);

        verify(entityManager).remove(type);
    }

    private TrainingType constructTrainingType() {
        return TrainingType.builder()
                .trainingTypeName("some awesome kind of training")
                .build();
    }
}