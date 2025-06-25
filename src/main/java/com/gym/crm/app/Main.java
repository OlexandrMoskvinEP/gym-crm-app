package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.repository.JSONStorageHandler;
import org.apache.catalina.LifecycleException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.refresh();
        String project = context.getEnvironment().getProperty("project.name");
        String version = context.getEnvironment().getProperty("project.version");
        System.out.println("Start project: " + project + " " + version);

        JSONStorageHandler storageHandler = context.getBean(JSONStorageHandler.class);

        System.out.println("Trainees loaded: " + storageHandler.getTraineeStorage().size());
        System.out.println("Trainers loaded: " + storageHandler.getTrainerStorage().size());
        System.out.println("Trainings loaded: " + storageHandler.getTrainingStorage().size());


        context.close();
    }
}
