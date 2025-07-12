package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.exception.EntityNotFoundException;
import com.gym.crm.app.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final TransactionExecutor txExecutor;

    public UserRepositoryImpl(EntityManagerFactory emf) {
        this.txExecutor = new TransactionExecutor(emf);
    }

    @Override
    public User save(User user) {
        log.debug("Saving user with username: {}", user.getUsername());

        return txExecutor.performReturningWithinTx(em -> {
            em.persist(user);

            return user;
        });
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return txExecutor.performReturningWithinTx(em ->
                em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst()
        );
    }

    @Override
    public void update(User user) {
        log.debug("Updating user with username: {}", user.getUsername());

        txExecutor.performWithinTx(em -> em.merge(user));
    }

    @Override
    public void deleteByUsername(String username) {
        log.debug("Deleting user with username: {}", username);

        txExecutor.performWithinTx(em -> {
            Optional<User> userOpt = em.createQuery(
                            "SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getResultStream()
                    .findFirst();

            userOpt.ifPresent(user -> {
                User managed = em.contains(user) ? user : em.merge(user);
                em.remove(managed);
                log.debug("User deleted: {}", username);
            });
        });
    }

    @Override
    public List<User> findAll() {
        return txExecutor.performReturningWithinTx(entityManager ->
                entityManager.createQuery("SELECT u FROM User u", User.class)
                        .getResultList()
        );
    }

    @Override
    public void updatePassword(String username, String encodedPassword) {
        txExecutor.performWithinTx(session -> {
            Query<?> query = (Query<?>) session.createQuery("""
                        UPDATE User u SET u.password = :password WHERE u.username = :username
                    """);
            query.setParameter("password", encodedPassword);
            query.setParameter("username", username);

            int updatedRows = query.executeUpdate();

            if (updatedRows == 0) {
                throw new EntityNotFoundException("User not found: " + username);
            }
        });
    }

    @Override
    public void changeStatus(String username) {
        log.debug("Changing user`s with username {} status", username);

        txExecutor.performWithinTx(session -> {
            Boolean isActive = session.createQuery("""
                                SELECT u.isActive FROM User u WHERE u.username = :username
                            """, Boolean.class)
                    .setParameter("username", username)
                    .getSingleResult();

            if (isActive == null) {
                throw new EntityNotFoundException("User not found: " + username);
            }

            Query<?> query = (Query<?>) session.createQuery("""
                        UPDATE User u SET u.isActive = :status WHERE u.username = :username
                    """);
            query.setParameter("status", !isActive);
            query.setParameter("username", username);

            int updatedRows = query.executeUpdate();

            if (updatedRows == 0) {
                throw new EntityNotFoundException("User not found: " + username);
            }
        });
    }
}
