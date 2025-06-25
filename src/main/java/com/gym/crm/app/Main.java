package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import org.apache.catalina.LifecycleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(AppConfig.class);
        context.refresh();
        String project = context.getEnvironment().getProperty("project.name");
        String version = context.getEnvironment().getProperty("project.version");

        System.out.println("Start project: " + project + " " + version);

        context.close();
    }
}
