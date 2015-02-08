package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Properties;

public final class ConfigProviders {
    public static ConfigProvider copyOf(Map<String, String> value) {
        return new ImmutableConfigProvider(ImmutableMap.copyOf(value), null);
    }

    private static ConfigProvider copyOf(Map<String, String> value, Object type) {
        return new ImmutableConfigProvider(ImmutableMap.copyOf(value), type);
    }

    public static ConfigProvider propertiesCopy(Properties properties) {
        return new ImmutableConfigProvider(Maps.fromProperties(properties), null);
    }

    public static ConfigProvider systemPropertiesCopy() {
        return copyOf(Maps.fromProperties(System.getProperties()), SystemConfigSources.SYSTEM_PROPERTIES);
    }

    public static ConfigProvider environmentCopy() {
        return copyOf(System.getenv(), SystemConfigSources.ENVIRONMENT);
    }

    /**
     * @throws java.lang.IllegalArgumentException if resource cannot be found
     */
    public static ConfigProvider copyOfPropertyResource(String resourceName) throws IOException {
        Properties properties = new Properties();
        URL resource = Resources.getResource(resourceName);
        try (InputStream is = resource.openStream()) {
            properties.load(is);
        }
        return copyOf(Maps.fromProperties(properties), resource);
    }

    public static ConfigProvider copyOfPropertyFile(File file) throws IOException {
        Properties properties = new Properties();
        try (InputStream is = new FileInputStream(file)) {
            properties.load(is);
        }
        return copyOf(Maps.fromProperties(properties), file);
    }

    /*package*/ static class ImmutableConfigProvider implements ConfigProvider {
        private final ImmutableMap<String, String> value;
        private final Object source;

        public ImmutableConfigProvider(ImmutableMap<String, String> value, Object source) {
            this.value = value;
            this.source = source;
        }

        @Nonnull
        @Override
        public ImmutableMap<String, String> get() {
            return value;
        }

        @Override
        public String toString() {
            return "ImmutableConfigProvider{" +
                    "source=" + source +
                    '}';
        }
    }

    private enum SystemConfigSources {
        /**
         * contains a point in time copy of java system properties
         */
        SYSTEM_PROPERTIES,
        /**
         * contains a point in time copy of env variables
         * (some/most/all jvms already creates a copy, though it can be modified via reflection)
         */
        ENVIRONMENT
    }
}
