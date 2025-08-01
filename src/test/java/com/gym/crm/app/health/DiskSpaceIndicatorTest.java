package com.gym.crm.app.health;

import com.gym.crm.app.health.common.DiscSpaceProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DiskSpaceIndicatorTest {
    @Mock
    private DiscSpaceProvider discSpaceProvider;
    @InjectMocks
    private DiskSpaceIndicator indicator;

    @Test
    void shouldReturnUpIfSpaceEnough() {
        Health.Builder builder = new Health.Builder();
        ReflectionTestUtils.setField(indicator, "threshold", 100_000L);

        when(discSpaceProvider.getFreeSpace()).thenReturn(550_000L);

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.UP, health.getStatus());
        assertTrue((Long) health.getDetails().get("freeDiskSpace") > 0);
    }

    @Test
    void shouldReturnDownIfSpaceNotEnough() {
        Health.Builder builder = new Health.Builder();
        ReflectionTestUtils.setField(indicator, "threshold", 500_000L);

        when(discSpaceProvider.getFreeSpace()).thenReturn(100_000L);

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue((Long) health.getDetails().get("freeDiskSpace") >= 0);
    }
}