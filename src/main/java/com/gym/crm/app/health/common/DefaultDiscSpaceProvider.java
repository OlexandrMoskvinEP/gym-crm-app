package com.gym.crm.app.health.common;

import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DefaultDiscSpaceProvider implements DiscSpaceProvider {
    @Override
    public long getFreeSpace() {
        return new File("/").getFreeSpace();
    }
}

