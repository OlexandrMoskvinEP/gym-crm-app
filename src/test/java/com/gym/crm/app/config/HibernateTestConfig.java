package com.gym.crm.app.config;

import com.gym.crm.app.YamlPropertySourceFactory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
public class HibernateTestConfig {
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
    public EntityManagerFactory entityManagerFactory(DataSource dataSource, Environment env) {
        Map<String, Object> properties = new HashMap<>();

        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.connection.datasource", dataSource);

        StandardServiceRegistry registry = new org.hibernate.boot.registry.StandardServiceRegistryBuilder()
                .applySettings(properties)
                .build();

        MetadataSources sources = new org.hibernate.boot.MetadataSources(registry)
                .addAnnotatedClass(com.gym.crm.app.domain.model.User.class)
                .addAnnotatedClass(com.gym.crm.app.domain.model.Trainer.class)
                .addAnnotatedClass(com.gym.crm.app.domain.model.Trainee.class)
                .addAnnotatedClass(com.gym.crm.app.domain.model.Training.class)
                .addAnnotatedClass(com.gym.crm.app.domain.model.TrainingType.class);

        org.hibernate.boot.Metadata metadata = sources.buildMetadata();

        return metadata.buildSessionFactory();
    }
}

