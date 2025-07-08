package com.gym.crm.app.config;

import com.gym.crm.app.YamlPropertySourceFactory;
import liquibase.integration.spring.SpringLiquibase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Configuration
@PropertySource(value = "classpath:application.yml", factory = YamlPropertySourceFactory.class)
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
