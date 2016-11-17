/*
 * Copyright 2015-2016 Canoo Engineering AG.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.canoo.dolphin.server.config;

import com.canoo.dolphin.server.impl.UnstableFeatureFlags;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import java.util.logging.Level;

/**
 * This class loads a Dolphin Platform configuration (see {@link DolphinPlatformConfiguration}) based on a property file.
 * The file must be placed under "META-INF/dolphin.properties" (normal for a JAR) or under
 * "WEB-INF/classes/META-INF/dolphin.properties" (normal for a WAR). If no file can be found a default
 * confihuration will be returned.
 * <p>
 * Currently the following properties will be supported in the "dolphin.properties" file
 * <p>
 * - openDolphinLogLevel that defines the level of the remoting logging (supported: "INFO", "WARN", etc.)
 * - servletMapping that defines the endpoint of the Dolphin Platform servlet
 * - useCrossSiteOriginFilter (true / false) that defines if a cross site origin filter should be used
 * <p>
 * All properties that are not specified in the property file will be defined by default values.
 */
public class ConfigurationFileLoader {

    private static final String JAR_LOCATION = "META-INF/dolphin.properties";

    private static final String WAR_LOCATION = "WEB-INF/classes/" + JAR_LOCATION;

    private static final String OPEN_DOLPHIN_LOG_LEVEL = "openDolphinLogLevel";

    private static final String DOLPHIN_PLATFORM_SERVLET_MAPPING = "servletMapping";

    private static final String USE_CROSS_SITE_ORIGIN_FILTER = "useCrossSiteOriginFilter";

    private static final String USE_SESSION_INVALIDATION_SERVLET = "useSessionInvalidationServlet";

    private static final String USE_HEALTH_CHECK = "useHealthCheck";

    private static final String HEALTH_CHECK_SERVLET_MAPPING = "HealthCheckServletMapping";

    private static final String GARBAGE_COLLECTION_ACTIVE = "garbageCollectionActive";

    private static final String SESSION_TIMEOUT = "sessionTimeout";

    private static final String MAX_CLIENTS_PER_SESSION = "maxClientsPerSession";

    private static final String ID_FILTER_URL_MAPPINGS = "idFilterUrlMappings";

    private static final String ROOT_PACKAGE_FOR_CLASSPATH_SCAN = "rootPackageForClasspathScan";

    private static final String MBEAN_REGISTRATION = "mBeanRegistration";

    /**
     * Tries to load a {@link DolphinPlatformConfiguration} based on a file. if no config file
     * can be found a default config will be returned.
     *
     * @return a configuration
     * @throws IOException if an exception is thrown while reading the file
     */
    public static DolphinPlatformConfiguration load() throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try (InputStream inputStream = classLoader.getResourceAsStream(JAR_LOCATION)) {
            if (inputStream != null) {
                return readConfig(inputStream);
            }
        }

        try (InputStream inputStream = classLoader.getResourceAsStream(WAR_LOCATION)) {
            if (inputStream == null) {
                return getDefaultConfig();
            } else {
                return readConfig(inputStream);
            }
        }
    }

    private static DolphinPlatformConfiguration getDefaultConfig() {
        return new DolphinPlatformConfiguration();
    }

    private static DolphinPlatformConfiguration readConfig(InputStream input) throws IOException {
        Properties prop = new Properties();
        prop.load(input);

        DolphinPlatformConfiguration configuration = new DolphinPlatformConfiguration();

        if (prop.containsKey(OPEN_DOLPHIN_LOG_LEVEL)) {
            String level = prop.getProperty(OPEN_DOLPHIN_LOG_LEVEL);
            if (level.trim().toLowerCase().equals("info")) {
                configuration.setOpenDolphinLogLevel(Level.INFO);
            } else if (level.trim().toLowerCase().equals("severe")) {
                configuration.setOpenDolphinLogLevel(Level.SEVERE);
            } else if (level.trim().toLowerCase().equals("all")) {
                configuration.setOpenDolphinLogLevel(Level.ALL);
            } else if (level.trim().toLowerCase().equals("config")) {
                configuration.setOpenDolphinLogLevel(Level.CONFIG);
            } else if (level.trim().toLowerCase().equals("fine")) {
                configuration.setOpenDolphinLogLevel(Level.FINE);
            } else if (level.trim().toLowerCase().equals("finer")) {
                configuration.setOpenDolphinLogLevel(Level.FINER);
            } else if (level.trim().toLowerCase().equals("finest")) {
                configuration.setOpenDolphinLogLevel(Level.FINEST);
            } else if (level.trim().toLowerCase().equals("off")) {
                configuration.setOpenDolphinLogLevel(Level.OFF);
            } else if (level.trim().toLowerCase().equals("warning")) {
                configuration.setOpenDolphinLogLevel(Level.WARNING);
            }
        }
        if (prop.containsKey(DOLPHIN_PLATFORM_SERVLET_MAPPING)) {
            configuration.setDolphinPlatformServletMapping(prop.getProperty(DOLPHIN_PLATFORM_SERVLET_MAPPING));
        }
        if (prop.containsKey(ROOT_PACKAGE_FOR_CLASSPATH_SCAN)) {
            configuration.setRootPackageForClasspathScan(prop.getProperty(ROOT_PACKAGE_FOR_CLASSPATH_SCAN));
        }
        if (prop.containsKey(MBEAN_REGISTRATION)) {
            configuration.setMBeanRegistration(Boolean.parseBoolean(prop.getProperty(MBEAN_REGISTRATION)));
        }
        if (prop.containsKey(USE_CROSS_SITE_ORIGIN_FILTER)) {
            configuration.setUseCrossSiteOriginFilter(Boolean.parseBoolean(prop.getProperty(DOLPHIN_PLATFORM_SERVLET_MAPPING)));
        }
        if (prop.containsKey(USE_SESSION_INVALIDATION_SERVLET)) {
            configuration.setUseSessionInvalidationServlet(Boolean.parseBoolean(prop.getProperty(USE_SESSION_INVALIDATION_SERVLET)));
        }
        if (prop.containsKey(GARBAGE_COLLECTION_ACTIVE)) {
            UnstableFeatureFlags.setUseGc(Boolean.parseBoolean(prop.getProperty(GARBAGE_COLLECTION_ACTIVE)));
        }
        if (prop.containsKey(SESSION_TIMEOUT)) {
            configuration.setSessionTimeout(Integer.parseInt(prop.getProperty(SESSION_TIMEOUT)));
        }
        if (prop.containsKey(MAX_CLIENTS_PER_SESSION)) {
            configuration.setMaxClientsPerSession(Integer.parseInt(prop.getProperty(MAX_CLIENTS_PER_SESSION)));
        }
        if (prop.containsKey(ID_FILTER_URL_MAPPINGS)) {
            String content = prop.getProperty(ID_FILTER_URL_MAPPINGS);
            configuration.setIdFilterUrlMappings(Arrays.asList(content.split(",")));
        }
        if (prop.containsKey(HEALTH_CHECK_SERVLET_MAPPING)) {
            configuration.setDolphinHealthCheckServletMapping(prop.getProperty(HEALTH_CHECK_SERVLET_MAPPING));
        }
        if (prop.containsKey(USE_HEALTH_CHECK)) {
            configuration.setUseHealthCheck(Boolean.parseBoolean(prop.getProperty(USE_HEALTH_CHECK)));
        }
        return configuration;
    }
}
