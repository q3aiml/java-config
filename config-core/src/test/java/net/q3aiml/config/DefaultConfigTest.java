package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Map;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultConfigTest {
    Map<String, String> configMap = ImmutableMap.<String, String>builder()
            .put("astring", "stringvalue")
            .put("number", "12345")
            .build();
    CascadingCompositeConfig configProvider = configProvider();
    DefaultConfig config = new DefaultConfig(configProvider);

    public CascadingCompositeConfig configProvider() {
        CascadingCompositeConfig configProvider = mock(CascadingCompositeConfig.class);
        when(configProvider.get()).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return ImmutableMap.copyOf(configMap);
            }
        });
        return configProvider;
    }

    @Test
    public void addConfigWithHigestPriorityTest() {
        ConfigProvider newConfig = mock(ConfigProvider.class);
        config.addConfigWithHighestPriority(newConfig);
        verify(configProvider).addConfigWithHighestPriority(newConfig);
    }

    @Test
    public void addConfigWithLowestPriorityTest() {
        ConfigProvider newConfig = mock(ConfigProvider.class);
        config.addConfigWithLowestPriority(newConfig);
        verify(configProvider).addConfigWithLowestPriority(newConfig);
    }

    @Test
    public void getStringTest() {
        assertEquals("stringvalue", config.getString("astring"));
    }

    @Test(expected = NoSuchElementException.class)
    public void getStringNotFoundTest() {
        config.getString("notset");
    }

    @Test
    public void getStringWithDefaultTest() {
        assertEquals("stringvalue", config.getString("astring", "default"));
    }

    @Test
    public void getStringUseDefaultTest() {
        assertEquals("default", config.getString("notset", "default"));
    }

    @Test
    public void getIntTest() {
        assertEquals(12345, config.getInt("number"));
    }

    @Test(expected = NoSuchElementException.class)
    public void getIntNotFoundThrowsTest() {
        config.getInt("notset");
    }

    @Test(expected = NumberFormatException.class)
    public void getIntNoANumberThrowsTest() {
        config.getInt("astring");
    }

    @Test
    public void getIntWithDefaultTest() {
        assertEquals(12345, (int)config.getInt("number", 5));
    }

    @Test
    public void getIntUseDefaultTest() {
        assertEquals(5, (int)config.getInt("notset", 5));
    }

}