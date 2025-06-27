package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import com.gym.crm.app.domain.dto.TrainingDto;
import com.gym.crm.app.domain.model.TrainingType;
import com.gym.crm.app.exception.UnacceptableOperationException;
import com.gym.crm.app.facade.GymFacade;
import org.apache.catalina.LifecycleException;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.time.LocalDate;

public class Main {

    public static void main(String[] args) throws  UnacceptableOperationException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.refresh();

        GymFacade gymFacade = context.getBean(GymFacade.class);

        context.close();
    }
}
