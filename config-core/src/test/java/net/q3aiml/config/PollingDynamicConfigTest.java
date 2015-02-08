package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.config.scheduler.PollingScheduler;
import net.q3aiml.config.source.PollingConfigSource;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class PollingDynamicConfigTest {
    PollingConfigSource source = mock(PollingConfigSource.class);
    PollingScheduler scheduler = mock(PollingScheduler.class);

    @Test
    public void initialLoadTest() {
        ImmutableMap<String, String> expectedValue = ImmutableMap.of(
                "initialload", "values"
        );
        when(source.poll()).thenReturn(PollResult.fullUpdate(expectedValue));
        PollingDynamicConfig config = new PollingDynamicConfig(source, scheduler);
        ArgumentCaptor<Runnable> runnable = ArgumentCaptor.forClass(Runnable.class);
        verify(scheduler).start(runnable.capture());
        assertEquals(expectedValue, config.get());

        runnable.getValue().run();
        verify(source, times(2)).poll();
    }

    @Test
    public void initialLoadPassesUpExceptionsTest() {
        RuntimeException expectedException = new RuntimeException("it failed");
        when(source.poll()).thenThrow(expectedException);
        try {
            new PollingDynamicConfig(source, scheduler);
        } catch (Exception e) {
            assertEquals(expectedException, e);
        }
    }

    @Test
    public void testName() throws Exception {
        when(source.poll()).thenReturn(PollResult.fullUpdate(ImmutableMap.<String, String>of()));
        PollingDynamicConfig config = new PollingDynamicConfig(source, scheduler);
        when(source.poll()).thenThrow(new RuntimeException("and now I failed, but I will be caught and logged"));
        // but later polling doesn't throw to avoid possibly killing the scheduler
        config.pollingLoadRunnable().run();
    }
}