package com.canoo.impl.dp.logging;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.logging.DolphinLoggerConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class LoggingConfigurationFileLoader {

    private static final String DEFAULT_LOCATION = "logging.properties";

    private static final String JAR_LOCATION = "META-INF/logging.properties";

    private static final String WAR_LOCATION = "WEB-INF/classes/" + JAR_LOCATION;

    private LoggingConfigurationFileLoader() {
    }

    public static DolphinLoggerConfiguration loadConfiguration() {
        final DolphinLoggerConfiguration configuration = createConfiguration();
        Assert.requireNonNull(configuration, "configuration");
        return configuration;
    }

    private static DolphinLoggerConfiguration createConfiguration() {
        try {
            final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            try (final InputStream inputStream = classLoader.getResourceAsStream(DEFAULT_LOCATION)) {
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
            }

            try (final InputStream inputStream = classLoader.getResourceAsStream(JAR_LOCATION)) {
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
            }

            try (final InputStream inputStream = classLoader.getResourceAsStream(WAR_LOCATION)) {
                if (inputStream != null) {
                    return readConfig(inputStream);
                }
            } catch (final Exception e) {
            }

            return new DolphinLoggerConfiguration(new Properties());
        } catch (final Exception e) {
            throw new RuntimeException("Can not create configuration!", e);
        }
    }

    private static DolphinLoggerConfiguration readConfig(final InputStream input) throws IOException {
        Assert.requireNonNull(input, "input");
        final Properties prop = new Properties();
        prop.load(input);

        return new DolphinLoggerConfiguration(prop);
    }
}
