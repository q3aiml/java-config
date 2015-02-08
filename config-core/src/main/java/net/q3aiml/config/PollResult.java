package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * The result of {@link net.q3aiml.config.source.PollingConfigSource#poll() polling} a config source for updates.
 */
public class PollResult {
    private final UpdateType updateType;
    private final ImmutableMap<String, String> result;

    /**
     * contains the full state of the polled resource, not just the state since the last update
     */
    public static PollResult fullUpdate(Map<String, String> result) {
        return new PollResult(UpdateType.FULL_UPDATE, ImmutableMap.copyOf(result));
    }

    private PollResult(UpdateType updateType, ImmutableMap<String, String> result) {
        this.updateType = updateType;
        this.result = result;
    }

    public UpdateType updateType() {
        return updateType;
    }

    public ImmutableMap<String, String> result() {
        return result;
    }

    public enum UpdateType {
        FULL_UPDATE,
    }
}
