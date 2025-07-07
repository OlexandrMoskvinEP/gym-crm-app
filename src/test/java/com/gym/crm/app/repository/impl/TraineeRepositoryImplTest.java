package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.Trainee;
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

import static com.gym.crm.app.data.mapper.TraineeMapper.constructTrainee;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeRepositoryImplTest {

    @Mock
    EntityManagerFactory entityManagerFactory;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction tx;

    @InjectMocks
    TraineeRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(tx);
        repository = new TraineeRepositoryImpl(entityManagerFactory);
    }

    @Test
    void shouldSaveAndFindTrainee() {
        Trainee trainee = constructTrainee();

        doNothing().when(entityManager).persist(trainee);

        TypedQuery<Trainee> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(trainee));

        repository.save(trainee);

        Optional<Trainee> found = repository.findById(2L);

        verify(entityManager).persist(trainee);
        assertTrue(found.isPresent());
    }

    @Test
    void shouldUpdateTrainee() {
        Trainee trainee = constructTrainee();

        when(entityManager.merge(trainee)).thenReturn(trainee);

        repository.update(trainee);

        verify(entityManager).merge(trainee);
    }
    @Test
    void shouldDeleteByIdById() {
        Trainee trainee = constructTrainee();


        TypedQuery<Trainee> mockQuery = mock(TypedQuery.class);

        when(entityManager.createQuery(anyString(), eq(Trainee.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("id"), anyLong())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(trainee));

        when(entityManager.contains(trainee)).thenReturn(false);
        when(entityManager.merge(trainee)).thenReturn(trainee);

        doNothing().when(entityManager).remove(trainee);

        repository.deleteById(5L);

        verify(entityManager).remove(trainee);
    }
}