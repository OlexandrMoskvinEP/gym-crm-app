package com.gym.crm.app.health.common.impl;

import com.gym.crm.app.health.common.MemoryStatsProvider;
import org.springframework.stereotype.Component;

@Component
public class DefaultMemoryStatsProvider implements MemoryStatsProvider {
    @Override
    public long getFreeMemory() {
        return Runtime.getRuntime().freeMemory();
    }
}
