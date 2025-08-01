package com.gym.crm.app.health;

import com.gym.crm.app.health.common.DiscSpaceProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DiskSpaceIndicator extends AbstractHealthIndicator {
    private final DiscSpaceProvider discSpaceProvider;

    @Value("${management.health.disk.threshold:1073741824}")
    private long threshold;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        long freeSpace = discSpaceProvider.getFreeSpace();

        if (freeSpace >= threshold) {
            builder.up().withDetail("freeDiskSpace", freeSpace);
        } else {
            builder.down()
                    .withDetail("freeDiskSpace", freeSpace)
                    .withDetail("threshold", threshold);
        }
    }
}
