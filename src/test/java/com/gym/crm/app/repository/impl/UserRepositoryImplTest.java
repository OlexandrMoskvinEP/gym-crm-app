package com.gym.crm.app.repository.impl;

import com.gym.crm.app.domain.model.User;
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

import static com.gym.crm.app.data.mapper.UserMapper.constructUser;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserRepositoryImplTest {

    @Mock
    EntityManagerFactory entityManagerFactory;

    @Mock
    EntityManager entityManager;

    @Mock
    EntityTransaction tx;

    @InjectMocks
    UserRepositoryImpl userRepository;

    @BeforeEach
    void setUp() {
        when(entityManagerFactory.createEntityManager()).thenReturn(entityManager);
        when(entityManager.getTransaction()).thenReturn(tx);
        userRepository = new UserRepositoryImpl(entityManagerFactory);
    }

    @Test
    void shouldSaveAndFindUser() {
        User user = constructUser();

        doNothing().when(entityManager).persist(user);

        TypedQuery<User> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("username"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(user));

        userRepository.save(user);

        Optional<User> found = userRepository.findByUsername(user.getUsername());

        verify(entityManager).persist(user);
        assertTrue(found.isPresent());
    }

    @Test
    void shouldUpdateUser() {
        User user = constructUser();

        when(entityManager.merge(user)).thenReturn(user);

        userRepository.update(user);

        verify(entityManager).merge(user);
    }

    @Test
    void shouldDeleteByUsername() {
        User user = constructUser();

        // найдем его по JPQL
        TypedQuery<User> mockQuery = mock(TypedQuery.class);
        when(entityManager.createQuery(anyString(), eq(User.class))).thenReturn(mockQuery);
        when(mockQuery.setParameter(eq("username"), anyString())).thenReturn(mockQuery);
        when(mockQuery.getResultStream()).thenReturn(Stream.of(user));

        when(entityManager.contains(user)).thenReturn(false);
        when(entityManager.merge(user)).thenReturn(user);

        doNothing().when(entityManager).remove(user);

        userRepository.deleteByUsername(user.getUsername());

        verify(entityManager).remove(user);
    }
}
