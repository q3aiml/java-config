package net.q3aiml.config.scheduler;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Polling on a fixed schedule.
 */
public class FixedDelayPollingScheduler implements PollingScheduler {
    protected final long delay;
    protected final TimeUnit units;
    protected final ScheduledExecutorService executor = createExecutor();

    /**
     * @param delay how long to wait between polls in {@code delayUnits}
     */
    public FixedDelayPollingScheduler(long delay, TimeUnit delayUnits) {
        this.delay = delay;
        this.units = delayUnits;
    }

    protected ScheduledExecutorService createExecutor() {
        return Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
            @Override
            public Thread newThread(@Nonnull Runnable r) {
                Thread thread = new Thread(r, "configuration poller with fixed delay");
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    @Override
    public void start(Runnable doPolling) {
        // not using separate initial delay as PollingDynamicConfig already does the first polling during creation
        executor.scheduleWithFixedDelay(doPolling, delay, delay, units);
    }

    public void stop() {
        executor.shutdown();
    }

    public boolean isRunning() {
        return !executor.isShutdown();
    }

    @Override
    public String toString() {
        return "FixedPollingScheduler{" +
                "delay=" + delay +
                ", units=" + units +
                '}';
    }
}
