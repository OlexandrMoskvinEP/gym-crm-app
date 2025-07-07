package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.impl.UserRepositoryImpl;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.sql.SQLException;
import java.util.Optional;

public class Main {
    public static void main(String[] args) throws SQLException {
//    System.setProperty("env", "test");

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.register(AppConfig.class);
        context.refresh();

    context.close();
    }
}
