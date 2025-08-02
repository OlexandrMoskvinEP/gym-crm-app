package com.gym.crm.app.health.common.impl;

import com.gym.crm.app.health.common.DiscSpaceProvider;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DefaultDiscSpaceProvider implements DiscSpaceProvider {
    @Override
    public long getFreeSpace() {
        return new File("/").getFreeSpace();
    }
}

