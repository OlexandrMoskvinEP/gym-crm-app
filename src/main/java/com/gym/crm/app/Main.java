package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.time.LocalDateTime;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Logger earlyLogger = LoggerFactory.getLogger("BOOT");
        earlyLogger.info("\n\n=========== NEW SESSION [{}] ===========\n", LocalDateTime.now());

        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();

        context.register(AppConfig.class);
        context.refresh();

        logger.info("Application started");

        context.close();

        logger.info("Application stopped");
    }
}
