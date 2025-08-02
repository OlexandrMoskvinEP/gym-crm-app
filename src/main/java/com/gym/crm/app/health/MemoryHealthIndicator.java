package com.gym.crm.app.health;

import com.gym.crm.app.health.common.MemoryStatsProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemoryHealthIndicator extends AbstractHealthIndicator {
    private final MemoryStatsProvider memoryStatsProvider;

    @Value("${management.health.memory.threshold:10485760}")
    private long threshold;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        long free = memoryStatsProvider.getFreeMemory();

        if (free >= threshold) {
            builder.up().withDetail("freeMemory", free).withDetail("threshold", threshold);
            return;
        }

        builder.down().withDetail("freeMemory", free).withDetail("threshold", threshold);
    }
}
