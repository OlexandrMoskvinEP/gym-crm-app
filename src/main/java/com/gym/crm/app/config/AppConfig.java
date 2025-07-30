package com.gym.crm.app.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

@Configuration
@EnableAspectJAutoProxy
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }



//    @Bean
//    public SpringLiquibase liquibase(DataSource dataSource, Environment environment) {
//        SpringLiquibase liquibase = new SpringLiquibase();
//
//        liquibase.setDataSource(dataSource);
//        liquibase.setChangeLog(environment.getProperty("liquibase.change-log"));
//
//        return liquibase;
//    }

//    @Bean
//    public RequestContextListener requestContextListener() {
//        return new RequestContextListener();
//    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Gym CRM API")
                        .version("1.0")
                        .description("API documentation"));
    }
}
