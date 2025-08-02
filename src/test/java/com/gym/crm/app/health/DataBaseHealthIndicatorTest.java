package com.gym.crm.app.health;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataBaseHealthIndicatorTest {
    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private DataSource dataSource;
    @Mock
    private Connection connection;
    @Mock
    private DatabaseMetaData metaData;
    @InjectMocks
    private DataBaseHealthIndicator indicator;

    @BeforeEach
    void setUp() {
        indicator = new DataBaseHealthIndicator(jdbcTemplate);
    }

    @Test
    void shouldReturnUpIfConnectionValid() throws SQLException {
        Health.Builder builder = new Health.Builder();

        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        when(connection.getMetaData()).thenReturn(metaData);
        when(metaData.getDatabaseProductName()).thenReturn("TestDB");

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.UP, health.getStatus());
        assertEquals("TestDB", health.getDetails().get("database"));
    }

    @Test
    void shouldReturnDownIfConnectionIsNotValid() throws Exception {
        Health.Builder builder = new Health.Builder();

        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isValid(anyInt())).thenReturn(true);
        when(connection.isValid(anyInt())).thenReturn(false);

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Connection is not valid", health.getDetails().get("error"));
    }

    @Test
    void shouldReturnDownOnException() throws Exception {
        Health.Builder builder = new Health.Builder();

        when(jdbcTemplate.getDataSource()).thenReturn(dataSource);
        when(dataSource.getConnection()).thenThrow(new SQLException("Test failure"));

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue(health.getDetails().get("error").toString().contains("Test failure"));
    }
}