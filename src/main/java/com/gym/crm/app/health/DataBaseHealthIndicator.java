package com.gym.crm.app.health;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Objects;

@RequiredArgsConstructor
@Component
public class DataBaseHealthIndicator extends AbstractHealthIndicator {
    protected final JdbcTemplate jdbcTemplate;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource(), "DataSource must not be null");

        try (Connection connection = dataSource.getConnection()) {
            if (!connection.isValid(1)) {
                builder.down().withDetail("error", "Connection is not valid");
                return;
            }

            builder.up().withDetail("database", connection.getMetaData().getDatabaseProductName());
        } catch (Exception ex) {
            builder.down(ex);
        }
    }
}
