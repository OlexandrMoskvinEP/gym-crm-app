package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.repository.impl.TraineeRepositoryImpl;
import com.gym.crm.app.repository.impl.TrainerRepositoryImpl;
import com.gym.crm.app.repository.impl.TrainingRepositoryImpl;
import org.apache.catalina.LifecycleException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.refresh();

        context.close();
    }
}
