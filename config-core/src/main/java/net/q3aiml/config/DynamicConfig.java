package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

/**
 * A {@link net.q3aiml.config.ConfigProvider} with a {@link ConfigProvider#get()} that may change.
 */
public interface DynamicConfig extends ConfigProvider {
    /**
     * Returns the values of this config (may change).
     * <p>
     * Any listener added with {@link #addUpdateListener(ConfigUpdateListener)} must be notified after
     * this function starts to return a new value.
     */
    @Nonnull
    @Override
    public ImmutableMap<String, String> get();

    public void addUpdateListener(ConfigUpdateListener listener);
    public void removeUpdateListener(ConfigUpdateListener listener);
}
