package com.gym.crm.app.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@Import({HibernateTestConfig.class})
public class TestConfig {
    @Bean
    public SpringLiquibase liquibase(DataSource dataSource, Environment environment) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog(environment.getProperty("liquibase.change-log"));
        liquibase.setContexts(environment.getProperty("liquibase.contexts", "test"));
        liquibase.setDropFirst(Boolean.parseBoolean(environment.getProperty("liquibase.drop-first", "true")));
        liquibase.setShouldRun(true);

        return liquibase;
    }
}
