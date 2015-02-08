package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import net.q3aiml.config.scheduler.PollingScheduler;
import net.q3aiml.config.source.PollingConfigSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Polls using a polling source and a scheduler.
 * <p/>
 * Notifies listeners of updates on the polling thread, blocking any future polling until
 * listeners have returned.
 */
public class PollingDynamicConfig extends SettableConfigProvider implements DynamicConfig {
    private static final Logger log = LoggerFactory.getLogger(PollingDynamicConfig.class);

    private final PollingConfigSource source;
    private final PollingScheduler scheduler;

    /**
     * Performs initial poll, throwing up any {@link net.q3aiml.config.source.PollingConfigSource}
     * exceptions, and starts scheduled polling.
     */
    public PollingDynamicConfig(PollingConfigSource source, PollingScheduler scheduler) {
        this.source = source;
        this.scheduler = scheduler;
        load();
        scheduler.start(pollingLoadRunnable());
    }

    public PollingConfigSource source() {
        return source;
    }

    public PollingScheduler scheduler() {
        return scheduler;
    }

    protected Runnable pollingLoadRunnable() {
        return new Runnable() {
            @Override
            public void run() {
                try {
                    load();
                } catch (Exception e) {
                    log.error("polling config update failed: " + e.getMessage(), e);
                }
            }
        };
    }

    protected void load() {
        PollResult poll = source.poll();
        PollResult.UpdateType updateType = poll.updateType();
        if (updateType == PollResult.UpdateType.FULL_UPDATE) {
            ImmutableMap<String, String> current = get();
            ImmutableMap<String, String> pollResult = poll.result();
            if (!current.equals(pollResult)) {
                set(pollResult);
            }
        } else {
            throw new IllegalArgumentException("unsupported poll result type " + updateType + " from " + source);
        }
    }
}
