package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;

/**
 * A {@link net.q3aiml.config.DynamicConfig} with a simple {@link #set(com.google.common.collect.ImmutableMap)}.
 */
public class SettableConfigProvider implements DynamicConfig {
    private volatile ImmutableMap<String, String> currentValue = ImmutableMap.of();
    private final ListenerManager listenerManager;

    public SettableConfigProvider() {
        this(new ListenerManager());
    }

    public SettableConfigProvider(ListenerManager listenerManager) {
        this.listenerManager = listenerManager;
    }

    @Nonnull
    public ImmutableMap<String, String> get() {
        return currentValue;
    }

    public void set(ImmutableMap<String, String> newValue) {
        ImmutableMap<String, String> previousValue = currentValue;
        this.currentValue = newValue;
        listenerManager.notifyListeners(previousValue, newValue, this);
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
