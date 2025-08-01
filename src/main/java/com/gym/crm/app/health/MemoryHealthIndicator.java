package com.gym.crm.app.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class MemoryHealthIndicator extends AbstractHealthIndicator {
    @Value("${management.health.memory.threshold:10485760}")
    private long threshold;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        long free = Runtime.getRuntime().freeMemory();

        if (free >= threshold) {
            builder.up().withDetail("freeMemory", free).withDetail("threshold", threshold);
        } else {
            builder.down().withDetail("freeMemory", free).withDetail("threshold", threshold);
        }
    }
}
