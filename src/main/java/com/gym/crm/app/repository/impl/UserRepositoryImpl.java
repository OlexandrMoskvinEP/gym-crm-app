package com.gym.crm.app.repository.impl;

import com.gym.crm.app.config.hibernate.TransactionExecutor;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.UserRepository;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private static final Logger log = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private TransactionExecutor txExecutor;

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
}
