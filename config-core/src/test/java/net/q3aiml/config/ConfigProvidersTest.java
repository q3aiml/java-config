package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;
import java.util.Random;

import static org.junit.Assert.*;

public class ConfigProvidersTest {
    @Rule
    public TemporaryFolder temp = new TemporaryFolder();

    @Test
    public void environmentCopyTest() {
        ConfigProvider configProvider = ConfigProviders.environmentCopy();
        assertEquals(ImmutableMap.copyOf(System.getenv()), configProvider.get());
    }

    @Test
    public void systemPropertiesCopyTest() {
        String testValue = String.valueOf(new Random().nextInt());
        System.setProperty("ConfigProvidersTest.testvalue", testValue);
        ConfigProvider configProvider = ConfigProviders.systemPropertiesCopy();
        assertEquals(testValue, configProvider.get().get("ConfigProvidersTest.testvalue"));
        System.setProperty("ConfigProvidersTest.testvalue", "othervalue");
        assertEquals(testValue, configProvider.get().get("ConfigProvidersTest.testvalue"));
    }

    @Test
    public void copyOfPropertyFileTest() throws IOException {
        Properties properties = new Properties();
        properties.put("test", "value");
        File file = temp.newFile();
        try (OutputStream os = new FileOutputStream(file)) {
            properties.store(os, "");
        }
        ConfigProvider configProvider = ConfigProviders.copyOfPropertyFile(file);
        assertEquals(ImmutableMap.of(
                "test", "value"
        ), configProvider.get());
    }

    @Test
    public void copyOfPropertyResourceTest() throws IOException {
        ConfigProvider config = ConfigProviders.copyOfPropertyResource("test.properties");
        assertEquals(ImmutableMap.of(
                "config", "test properties",
                "color", "red",
                "food", "taco",
                "salsa", "corn"
        ), config.get());
    }

}