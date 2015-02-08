package net.q3aiml.config.scheduler;

import org.junit.Test;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class FixedDelayPollingSchedulerTest {
    FixedDelayPollingScheduler scheduler = new FixedDelayPollingScheduler(1, TimeUnit.MILLISECONDS);
    boolean isDaemon;
    boolean inRunnableIsRunning;

    @Test
    public void startStopTest() throws InterruptedException {
        final Semaphore semaphore = new Semaphore(-5);
        scheduler.start(new Runnable() {
            @Override
            public void run() {
                isDaemon = Thread.currentThread().isDaemon();
                inRunnableIsRunning = scheduler.isRunning();
                semaphore.release();
            }
        });
        boolean acquired;
        do {
            acquired = semaphore.tryAcquire(1, TimeUnit.SECONDS);
        } while (!acquired && scheduler.isRunning());
        assertTrue(acquired);
        assertTrue(isDaemon);
        assertTrue(inRunnableIsRunning);
        scheduler.stop();
    }

    @Test
    public void stopNotStartedTest() throws Exception {
        scheduler.stop();
    }

    @Test
    public void toStringTest() throws Exception {
        assertThat(scheduler.toString().toLowerCase(), containsString("fixed"));
    }
}