package net.q3aiml.config;

import java.util.Map;

/**
 * Listens for updates to a {@link net.q3aiml.config.DynamicConfig}.
 */
public interface ConfigUpdateListener {
    /**
     * Notified when configuration has been updated (called after the update is finished on {@code source}).
     */
    public void configUpdated(Map<String, String> before, Map<String, String> after, DynamicConfig source);
}
