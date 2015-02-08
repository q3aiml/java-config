package net.q3aiml.config;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Combines {@link net.q3aiml.config.ConfigProvider}s into one in a defined priority order.
 * <p>
 * Works best for reads, not lots of updates.
 */
public class CascadingCompositeConfig implements DynamicConfig {
    protected volatile ImmutableList<ConfigProvider> configs;
    protected volatile ImmutableMap<String, String> compositeCache = ImmutableMap.of();
    protected final ListenerManager listenerManager = new ListenerManager();

    public CascadingCompositeConfig(ConfigProvider... configs) {
        this(ImmutableList.copyOf(configs));
    }

    /**
     * @param configs configs to combine is order of increasing priority (later configs override earlier configs)
     */
    public CascadingCompositeConfig(List<ConfigProvider> configs) {
        listenerManager.addUpdateListener(new ConfigUpdateListener() {
            @Override
            public void configUpdated(Map<String, String> before, Map<String, String> after, DynamicConfig source) {
                updateCache();
            }
        });

        this.configs = ImmutableList.copyOf(configs);

        for (ConfigProvider config : configs) {
            listenerManager.listenIfDynamic(config);
        }

        updateCache();
    }

    public synchronized void addConfigWithHighestPriority(ConfigProvider config) {
        ImmutableMap<String, String> before = compositeCache;
        configs = ImmutableList.<ConfigProvider>builder().addAll(configs).add(config).build();
        listenerManager.listenIfDynamic(config);
        updateCache();
        listenerManager.notifyListeners(before, compositeCache, this);
    }

    public synchronized void addConfigWithLowestPriority(ConfigProvider config) {
        ImmutableMap<String, String> before = compositeCache;
        configs = ImmutableList.<ConfigProvider>builder().add(config).addAll(configs).build();
        listenerManager.listenIfDynamic(config);
        updateCache();
        listenerManager.notifyListeners(before, compositeCache, this);
    }

    protected synchronized void updateCache() {
        Map<String, String> compositeBuilder = new HashMap<>();
        for (ConfigProvider config : configs) {
            /*
            TODO should later configs be able to shadow values from earlier configs with non-existence?
            would need separate interface that allows Optional<String> as value or something
             */
            ImmutableMap<String, String> currentValue = config.get();
            checkNotNull(currentValue, "config " + config + " returned null value");
            compositeBuilder.putAll(currentValue);
        }
        compositeCache = ImmutableMap.copyOf(compositeBuilder);
    }

    @Nonnull
    @Override
    public ImmutableMap<String, String> get() {
        return compositeCache;
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
