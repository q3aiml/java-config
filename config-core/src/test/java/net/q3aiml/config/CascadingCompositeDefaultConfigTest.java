package net.q3aiml.config;

import com.google.common.collect.ImmutableMap;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CascadingCompositeDefaultConfigTest {
    ConfigProvider firstConfig = mock(ConfigProvider.class);
    DynamicConfig secondConfig = mock(DynamicConfig.class);
    ConfigUpdateListener listener = mock(ConfigUpdateListener.class);

    ImmutableMap<String, String> firstAndSecondState = ImmutableMap.of(
            "config", "second config",
            "color", "blue",
            "food", "burrito"
    );

    @Before
    public void setUp() throws Exception {
        when(firstConfig.get()).thenReturn(ImmutableMap.of(
                "config", "first config",
                "color", "blue"
        ));
        when(secondConfig.get()).thenReturn(ImmutableMap.of(
                "config", "second config",
                "food", "burrito"
        ));
    }

    @Test
    public void laterConfigsHavePriorityTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        assertEquals(firstAndSecondState, config.get());
    }

    @Test
    public void cacheInvalidationTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        ArgumentCaptor<ConfigUpdateListener> listener = ArgumentCaptor.forClass(ConfigUpdateListener.class);

        verify(secondConfig).addUpdateListener(listener.capture());
        assertEquals(firstAndSecondState, config.get());

        when(secondConfig.get()).thenReturn(ImmutableMap.of("new", "newvalue"));
        listener.getValue().configUpdated(null, null, null);
        assertEquals(ImmutableMap.of(
                "config", "first config",
                "color", "blue",
                "new", "newvalue"
        ), config.get());
    }

    private static <T extends ConfigProvider> T stubGet(T provider) {
        when(provider.get()).thenReturn(ImmutableMap.of(
                "config", "added config",
                "salsa", "spicy"
        ));
        return provider;
    }

    @Test
    public void addConfigWithHighestPriorityTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        config.addUpdateListener(listener);
        ConfigProvider newConfig = stubGet(mock(ConfigProvider.class));
        config.addConfigWithHighestPriority(newConfig);

        ImmutableMap<String, String> newState = ImmutableMap.of(
                "config", "added config",
                "color", "blue",
                "food", "burrito",
                "salsa", "spicy"
        );
        assertEquals(newState, config.get());
        verify(listener).configUpdated(firstAndSecondState, newState, config);
    }

    @Test
    public void addConfigWithHighestPriorityDynamicTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        DynamicConfig newConfig = stubGet(mock(DynamicConfig.class));
        config.addConfigWithHighestPriority(newConfig);

        verify(newConfig).addUpdateListener(any(ConfigUpdateListener.class));
        assertEquals(ImmutableMap.of(
                "config", "added config",
                "color", "blue",
                "food", "burrito",
                "salsa", "spicy"
        ), config.get());
    }

    @Test
    public void addConfigWithLowestPriorityTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        config.addUpdateListener(listener);
        ConfigProvider newConfig = stubGet(mock(ConfigProvider.class));
        config.addConfigWithLowestPriority(newConfig);

        ImmutableMap<String, String> newState = ImmutableMap.of(
                "config", "second config",
                "color", "blue",
                "food", "burrito",
                "salsa", "spicy"
        );
        assertEquals(newState, config.get());
        verify(listener).configUpdated(firstAndSecondState, newState, config);
    }

    @Test
    public void addConfigWithLowestPriorityDynamicTest() {
        CascadingCompositeConfig config = new CascadingCompositeConfig(firstConfig, secondConfig);
        DynamicConfig newConfig = stubGet(mock(DynamicConfig.class));
        config.addConfigWithLowestPriority(newConfig);

        verify(newConfig).addUpdateListener(any(ConfigUpdateListener.class));
        assertEquals(ImmutableMap.of(
                "config", "second config",
                "color", "blue",
                "food", "burrito",
                "salsa", "spicy"
        ), config.get());
    }
}