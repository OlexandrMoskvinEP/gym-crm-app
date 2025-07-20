package com.gym.crm.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@ComponentScan(basePackages = "com.gym.crm.app.repository")
@Import({HibernateTestConfig.class})
public class TestConfig {
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, Environment environment) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(environment.getProperty("liquibase.change-log"));

        return liquibase;
    }
}
