package com.gym.crm.app.repository.impl;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import com.gym.crm.app.config.TestConfig;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DBRider
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = TestConfig.class)
@DBUnit(schema = "PUBLIC")
public abstract class AbstractRepositoryTest<T> {

    @Autowired
    protected T repository;

    @Autowired
    protected EntityManagerFactory entityManagerFactory;

    protected EntityManager entityManager;

    @BeforeEach
    void init() {
        this.entityManager = entityManagerFactory.createEntityManager();
    }
}
