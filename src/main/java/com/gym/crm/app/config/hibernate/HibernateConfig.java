package com.gym.crm.app.config.hibernate;

import com.gym.crm.app.YamlPropertySourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.gym.crm.app")
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class HibernateConfig {
    @Bean
    public DataSource dataSource(Environment env) {
        HikariConfig config = new HikariConfig();

        config.setDriverClassName(env.getProperty("database.driver"));
        config.setJdbcUrl(env.getProperty("database.url"));
        config.setUsername(env.getProperty("database.username"));
        config.setPassword(env.getProperty("database.password"));

        return new HikariDataSource(config);
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {
        return Persistence.createEntityManagerFactory("gym-unit");
    }

}
