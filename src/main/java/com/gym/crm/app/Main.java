package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.domain.model.User;
import com.gym.crm.app.repository.UserRepository;
import com.gym.crm.app.repository.impl.UserRepositoryImpl;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Optional;

public class Main {
    public static void main(String[] args) {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.register(AppConfig.class);
        context.refresh();

        UserRepositoryImpl userDao = context.getBean(UserRepositoryImpl.class);

        User user = User.builder()
                .firstName("John")
                .lastName("Doe")
                .username("john.doe")
                .password("hashedPassword123")
                .isActive(true)
                .build();

        // Сохранение
        userDao.save(user);
        System.out.println("User saved!");

         //Поиск
        Optional<User> found = userDao.findByUsername("john.doe");

        found.ifPresentOrElse(
                u -> System.out.println("Found user: " + u.getFirstName() + " " + u.getLastName()),
                () -> System.out.println("User not found")
        );

        context.close();
    }
}
