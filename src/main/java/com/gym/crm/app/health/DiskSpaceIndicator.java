package com.gym.crm.app.health;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@RequiredArgsConstructor
public class DiskSpaceIndicator extends AbstractHealthIndicator {
    @Value("${management.health.disk.threshold:1073741824}")
    private long threshold;

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        File root = new File("/");
        long freeSpace = root.getFreeSpace();

        if (freeSpace >= threshold) {
            builder.up().withDetail("freeDiskSpace", freeSpace);
        } else {
            builder.down()
                    .withDetail("freeDiskSpace", freeSpace)
                    .withDetail("threshold", threshold);
        }
    }
}
