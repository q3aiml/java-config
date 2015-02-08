package net.q3aiml.config;

import javax.annotation.Nonnull;

/**
 * The friendly facade of {@link ConfigProvider}.Provides at least some of the convenience methods
 * expected from things like this.
 */
public interface Config extends DynamicConfig {
    public void addConfigWithHighestPriority(ConfigProvider config);

    public void addConfigWithLowestPriority(ConfigProvider config);

    @Nonnull
    public String getString(String name);

    public String getString(String name, String defaultValue);

    public int getInt(String name);

    public Integer getInt(String name, Integer defaultValue);
}
