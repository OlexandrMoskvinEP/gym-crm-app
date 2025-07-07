package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

public class Main {
    public static void main(String[] args) {
        System.setProperty("env", "-local");

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.register(AppConfig.class);
        context.refresh();

        context.close();
    }
}
