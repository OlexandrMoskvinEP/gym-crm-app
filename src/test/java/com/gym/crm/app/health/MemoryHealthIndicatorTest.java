package com.gym.crm.app.health;

import com.gym.crm.app.health.common.MemoryStatsProvider;
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
class MemoryHealthIndicatorTest {
    @Mock
    private MemoryStatsProvider memoryStatsProvider;
    @InjectMocks
    private MemoryHealthIndicator indicator;

    @Test
    void shouldReturnUpIfSpaceEnough() throws Exception {
        Health.Builder builder = new Health.Builder();
        ReflectionTestUtils.setField(indicator, "threshold", 100_000_000L);

        when(memoryStatsProvider.getFreeMemory()).thenReturn(550_000_000L);

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.UP, health.getStatus());
        assertTrue((Long) health.getDetails().get("freeMemory") > 0);
    }

    @Test
    void shouldReturnDownIfSpaceNotEnough() throws Exception {
        Health.Builder builder = new Health.Builder();
        ReflectionTestUtils.setField(indicator, "threshold", 100_000_000L);

        when(memoryStatsProvider.getFreeMemory()).thenReturn(500_00_000L);

        indicator.doHealthCheck(builder);

        Health health = builder.build();
        assertEquals(Status.DOWN, health.getStatus());
        assertTrue((Long) health.getDetails().get("freeMemory") >= 0);
    }
}