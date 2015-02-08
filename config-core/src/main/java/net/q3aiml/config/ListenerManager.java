package net.q3aiml.config;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/*package*/ final class ListenerManager {
    protected final CopyOnWriteArrayList<ConfigUpdateListener> updateListeners = new CopyOnWriteArrayList<>();
    protected final ConfigUpdateListener dependentConfigListener = new DependentConfigListener(this);

    public void addUpdateListener(ConfigUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void removeUpdateListener(ConfigUpdateListener listener) {
        updateListeners.remove(listener);
    }

    public void notifyListeners(Map<String, String> before, Map<String, String> after, DynamicConfig source) {
        for (ConfigUpdateListener listener : updateListeners) {
            listener.configUpdated(before, after, source);
        }
    }

    public void dependentConfigUpdated(Map<String, String> before, Map<String, String> after, DynamicConfig source) {
        notifyListeners(before, after, source);
    }

    public void listenIfDynamic(ConfigProvider config) {
        if (config instanceof DynamicConfig) {
            ((DynamicConfig)config).addUpdateListener(dependentConfigListener);
        }
    }

    protected static class DependentConfigListener implements ConfigUpdateListener {
        private final WeakReference<ListenerManager> thisRef;

        public DependentConfigListener(ListenerManager thisBase) {
            this.thisRef = new WeakReference<>(thisBase);
        }

        @Override
        public void configUpdated(Map<String, String> before, Map<String, String> after, DynamicConfig source) {
            ListenerManager thisBase = thisRef.get();
            if (thisBase != null) {
                thisBase.dependentConfigUpdated(before, after, source);
            } else {
                source.removeUpdateListener(this);
            }
        }
    }
}
