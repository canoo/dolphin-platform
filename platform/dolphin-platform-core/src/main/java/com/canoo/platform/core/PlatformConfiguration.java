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
package com.canoo.platform.core;

import org.apiguardian.api.API;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.apiguardian.api.API.Status.MAINTAINED;

/**
 * Provides access to the configuration of the Dolphin Platform.
 */
@API(since = "0.x", status = MAINTAINED)
public interface PlatformConfiguration extends Serializable {

    /**
     * Returns true if the configuration of the Dolphin Platform contains the given property. Otherwise the method returns false.
     *
     * @param key name of the property
     * @return true if the configurations contains a property with the given name, otherwise false.
     */
    boolean containsProperty(final String key);

    Optional<String> property(final String key);

    /**
     * Returns the value of the property with the given name. If the configuration do not contain such a property the given default value will be returned.
     *
     * @param key name of the property
     * @param defaultValue the default value that will be returned if property is not defined
     * @return value of the property or the default value if the property is not defined
     */
    String getProperty(final String key, final String defaultValue);

    String getProperty(final String key);

    /**
     * Returns the value of the property with the given name. If the configuration do not contain such a property the given default value will be returned.
     *
     * @param key name of the property
     * @param defaultValue the default value that will be returned if property is not defined
     * @return value of the property or the default value if the property is not defined
     */
    boolean getBooleanProperty(final String key, boolean defaultValue);

    /**
     * Returns the value of the property with the given name. If the configuration do not contain such a property the given default value will be returned.
     *
     * @param key name of the property
     * @param defaultValue the default value that will be returned if property is not defined
     * @return value of the property or the default value if the property is not defined
     */
    int getIntProperty(final String key, int defaultValue);

    /**
     * Returns the value of the property with the given name. If the configuration do not contain such a property the given default value will be returned.
     *
     * @param key name of the property
     * @param defaultValue the default value that will be returned if property is not defined
     * @return value of the property or the default value if the property is not defined
     */
    long getLongProperty(final String key, long defaultValue);

    /**
     * Returns the value of the property with the given name. If the configuration do not contain such a property the given default value will be returned.
     *
     * @param key name of the property
     * @param defaultValues the default value that will be returned if property is not defined
     * @return value of the property or the default value if the property is not defined
     */
    List<String> getListProperty(final String key, final List<String> defaultValues);

    /**
     * Returns a set that contains the names of all definied properties.
     * @return set with all property names
     */
    Set<String> getPropertyKeys();
}
