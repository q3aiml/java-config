package net.q3aiml.config.source;


import net.q3aiml.config.PollResult;

/**
 * A source of configuration that can be polled for updates.
 */
public interface PollingConfigSource {
    /**
     * Polls the source and returns the result.
     */
    public PollResult poll();
}
