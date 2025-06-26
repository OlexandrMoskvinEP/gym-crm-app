package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.repository.TraineeRepository;
import com.gym.crm.app.repository.TrainerRepository;
import com.gym.crm.app.repository.TrainingRepository;
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

        TrainingRepository trainingRepo = context.getBean(TrainingRepository.class);
        TrainerRepository trainerRepo = context.getBean(TrainerRepository.class);
        TraineeRepository traineeRepo = context.getBean(TraineeRepository.class);

        System.out.println(trainerRepo.findAll());
        System.out.println(traineeRepo.findAll());
        System.out.println(trainingRepo.findAll());

        context.close();
    }
}
