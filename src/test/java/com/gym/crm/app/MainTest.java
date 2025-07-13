package com.gym.crm.app;

import com.gym.crm.app.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class MainTest {

    @Test
    void mainShouldStartAndCloseContext() {
        assertDoesNotThrow(() -> {
            AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
            context.register(AppConfig.class);
            context.refresh();
            context.close();
        });
    }
}