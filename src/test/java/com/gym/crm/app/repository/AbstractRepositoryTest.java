package com.gym.crm.app.repository;

import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.junit5.api.DBRider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@DBRider
@SpringBootTest
@DBUnit(schema = "PUBLIC")
@ActiveProfiles("test")
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
