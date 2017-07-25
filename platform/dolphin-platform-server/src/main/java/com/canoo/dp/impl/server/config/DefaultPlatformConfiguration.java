/*
 * Copyright 2015-2017 Canoo Engineering AG.
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
package com.canoo.dp.impl.server.config;

import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.platform.server.spi.PlatformConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public final class DefaultPlatformConfiguration implements PlatformConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultPlatformConfiguration.class);

    private final Properties internalProperties;

    public DefaultPlatformConfiguration() {
        this(new Properties());
        setIntProperty(DefaultModuleConfig.SESSION_TIMEOUT, DefaultModuleConfig.SESSION_TIMEOUT_DEFAULT_VALUE);
        setBooleanProperty(DefaultModuleConfig.USE_CROSS_SITE_ORIGIN_FILTER, DefaultModuleConfig.USE_CROSS_SITE_ORIGIN_FILTER_DEFAULT_VALUE);
        setBooleanProperty(DefaultModuleConfig.MBEAN_REGISTRATION, DefaultModuleConfig.M_BEAN_REGISTRATION_DEFAULT_VALUE);
        setProperty(DefaultModuleConfig.ROOT_PACKAGE_FOR_CLASSPATH_SCAN, DefaultModuleConfig.ROOT_PACKAGE_FOR_CLASSPATH_SCAN_DEFAULT_VALUE);
        setBooleanProperty(DefaultModuleConfig.PLATFORM_ACTIVE, DefaultModuleConfig.ACTIVE_DEFAULT_VALUE);
        setListProperty(DefaultModuleConfig.ACCESS_CONTROL_ALLOW_HEADERS, DefaultModuleConfig.ACCESS_CONTROL_ALLOW_HEADERS_DEFAULT_VALUE);
        setListProperty(DefaultModuleConfig.ACCESS_CONTROL_ALLOW_METHODS, DefaultModuleConfig.ACCESS_CONTROL_ALLOW_METHODS_DEFAULT_VALUE);
        setBooleanProperty(DefaultModuleConfig.ACCESS_CONTROL_ALLOW_CREDENTIALS, DefaultModuleConfig.ACCESS_CONTROL_ALLOW_CREDENTIALS_DEFAULT_VALUE);
        setLongProperty(DefaultModuleConfig.ACCESS_CONTROL_MAXAGE, DefaultModuleConfig.ACCESS_CONTROL_MAX_AGE_DEFAULT_VALUE);
        setIntProperty(DefaultModuleConfig.MAX_CLIENTS_PER_SESSION, DefaultModuleConfig.MAX_CLIENTS_PER_SESSION_DEFAULT_VALUE);
        setListProperty(DefaultModuleConfig.ID_FILTER_URL_MAPPINGS, DefaultModuleConfig.ID_FILTER_URL_MAPPINGS_DEFAULT_VALUE);
        setListProperty(DefaultModuleConfig.CORS_ENDPOINTS_URL_MAPPINGS, DefaultModuleConfig.CORS_ENDPOINTS_URL_MAPPINGS_DEFAULT_VALUE);
    }

    public DefaultPlatformConfiguration(final Properties internalProperties) {
        Assert.requireNonNull(internalProperties, "internalProperties");
        this.internalProperties = internalProperties;
    }

    public boolean containsProperty(final String key) {
        return internalProperties.containsKey(key);
    }

    public String getProperty(final String key) {
        return getProperty(key, null);
    }

    public boolean getBooleanProperty(final String key, final boolean defaultValue) {
        return Boolean.parseBoolean(internalProperties.getProperty(key, defaultValue + ""));
    }

    public boolean getBooleanProperty(final String key) {
        return getBooleanProperty(key, false);
    }

    public int getIntProperty(final String key, final int defaultValue) {
        return Integer.parseInt(internalProperties.getProperty(key, defaultValue + ""));
    }

    public long getLongProperty(final String key, final long defaultValue) {
        return Long.parseLong(internalProperties.getProperty(key, defaultValue + ""));
    }

    public List<String> getListProperty(final String key) {
        return getListProperty(key, Collections.<String>emptyList());
    }

    public List<String> getListProperty(final String key, final List<String> defaultValues) {
        final String value = internalProperties.getProperty(key);
        if (value != null) {
            return Arrays.asList(value.split(","));
        }
        return defaultValues;
    }

    public String getProperty(final String key, final String defaultValue) {
        return internalProperties.getProperty(key, defaultValue);
    }

    public Set<String> getPropertyKeys() {
        final Set<String> ret = new HashSet<>();
        for (final Object key : internalProperties.keySet()) {
            if (key != null) {
                ret.add(key.toString());
            }
        }
        return ret;
    }

    public void setIntProperty(final String key, final int value) {
        setProperty(key, Integer.toString(value));
    }

    public void setLongProperty(final String key, final long value) {
        setProperty(key, Long.toString(value));
    }

    public void setBooleanProperty(final String key, final boolean value) {
        setProperty(key, Boolean.toString(value));
    }

    public void setListProperty(final String key, final List<String> values) {
        if (values == null) {
            setProperty(key, null);
        } else if (values.isEmpty()) {
            setProperty(key, "");
        } else {
            final StringBuilder builder = new StringBuilder();
            for (String value : values) {
                builder.append(value + ", ");
            }
            builder.setLength(builder.length() - 2);
            setProperty(key, builder.toString());
        }
    }

    public void setProperty(final String key, final String value) {
        if (value == null) {
            LOG.warn("Setting property '{}' to null value will be ignored.");
        } else {
            internalProperties.setProperty(key, value);
        }
    }

    public void log() {
        final Set<Map.Entry<Object, Object>> properties = internalProperties.entrySet();
        for (Map.Entry property : properties) {
            LOG.debug("Dolphin Platform starts with value for " + property.getKey() + " = " + property.getValue());
        }
    }
}


