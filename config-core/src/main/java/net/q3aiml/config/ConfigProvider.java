package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

/**
 * Provides configuration in the form of a map of name strings to value strings.
 */
public interface ConfigProvider {
    /**
     * Returns the values of this config (must not change unless implements {@link net.q3aiml.config.DynamicConfig}).
     */
    @Nonnull
    public ImmutableMap<String, String> get();
}
