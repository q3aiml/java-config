package net.q3aiml.config;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Ints;

import javax.annotation.Nonnull;
import java.util.NoSuchElementException;

/**
 * A friendly facade around {@link ConfigProvider}.
 */
public class DefaultConfig implements Config {
    private final CascadingCompositeConfig config;
    private final ListenerManager listenerManager = new ListenerManager();

    public DefaultConfig(ConfigProvider config) {
        this(config, new ListenerManager());
    }

    @VisibleForTesting
    /*package*/ DefaultConfig(ConfigProvider config, ListenerManager listenerManager) {
        if (config == null) {
            throw new NullPointerException("ConfigProvider must not be null");
        }
        if (config instanceof CascadingCompositeConfig) {
            this.config = (CascadingCompositeConfig)config;
        } else {
            this.config = new CascadingCompositeConfig(ImmutableList.of(config));
        }
        listenerManager.listenIfDynamic(config);
    }

    @Override
    public void addConfigWithHighestPriority(ConfigProvider config) {
        this.config.addConfigWithHighestPriority(config);
    }

    @Override
    public void addConfigWithLowestPriority(ConfigProvider config) {
        this.config.addConfigWithLowestPriority(config);
    }

    protected String getStringInner(String name) {
        return config.get().get(name);
    }

    protected <T> T throwIfNull(String name, T value) {
        if (value == null) {
            throw new NoSuchElementException("no config value set for " + name);
        }
        return value;
    }

    @Nonnull
    @Override
    public ImmutableMap<String, String> get() {
        return config.get();
    }

    /**
     * Returns the string value of {@code name}.
     * @throws java.util.NoSuchElementException if no value exists for {@code name}
     */
    @Override
    @Nonnull
    public String getString(String name) {
        return throwIfNull(name, getStringInner(name));
    }

    @Override
    public String getString(String name, String defaultValue) {
        String value = getStringInner(name);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * Returns the integer value of {@code name}.
     * @throws java.util.NoSuchElementException if no value exists for {@code name}
     * @throws java.lang.NumberFormatException if {@code name} does not contain an integer
     */
    @Override
    public int getInt(String name) {
        return Integer.parseInt(throwIfNull(name, getString(name)));
    }

    /**
     * Returns the integer value of {@code name}, or {@code defaultValue} if it is not set or
     * {@code name} is not an int.
     */
    @Override
    public Integer getInt(String name, Integer defaultValue) {
        String stringValue = getStringInner(name);
        Integer value = stringValue != null ? Ints.tryParse(stringValue) : null;
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    @Override
    public void addUpdateListener(ConfigUpdateListener listener) {
        listenerManager.addUpdateListener(listener);
    }

    @Override
    public void removeUpdateListener(ConfigUpdateListener listener) {
        listenerManager.removeUpdateListener(listener);
    }
}
