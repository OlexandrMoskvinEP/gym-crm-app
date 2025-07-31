package com.gym.crm.app;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@RequiredArgsConstructor
@Configuration
public class ProfileLogger {
    private static final Logger log = LoggerFactory.getLogger(ProfileLogger.class);
    private final Environment env;

    @EventListener(ApplicationReadyEvent.class)
    public void logActiveProfile() {
        log.info("ACTIVE PROFILE: {}", (Object) env.getActiveProfiles());
    }
}
