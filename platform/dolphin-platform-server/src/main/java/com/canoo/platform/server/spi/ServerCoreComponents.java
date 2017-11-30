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
package com.canoo.platform.server.spi;

import com.canoo.platform.core.PlatformConfiguration;
import org.apiguardian.api.API;

import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Facade to all global server components that are provided by the Dolphin Platform
 *
 * @author Hendrik Ebbers
 */
@API(since = "0.x", status = EXPERIMENTAL)
public interface ServerCoreComponents {

    /**
     * Returns the configuration of the Dolphin Platform
     *
     * @return the configuration
     */
    PlatformConfiguration getConfiguration();

    /**
     * The {@link ServerCoreComponents} can hold implementations for a given service and share this between several modules (see {@link ServerModule}). This method must be used to add such an instance of a service to the core components.
     *
     * @param cls      the class of the service
     * @param instance the instance of the service
     * @param <T>      type of the service
     * @see ServerModule
     * @see #getInstance(Class)
     */
    <T> void provideInstance(Class<T> cls, T instance);

    /**
     * The {@link ServerCoreComponents} can hold implementations for a given service and share this between several modules (see {@link ServerModule}). This method must be used to access such an instance of a service.
     *
     * @param cls the class of the service
     * @param <T> type of the service
     * @return the instance of the service
     * @see ServerModule
     * @see #provideInstance(Class, Object)
     */
    <T> Optional<T> getInstance(Class<T> cls);
}
