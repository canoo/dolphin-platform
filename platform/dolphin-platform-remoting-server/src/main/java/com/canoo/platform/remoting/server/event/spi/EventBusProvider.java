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
package com.canoo.platform.remoting.server.event.spi;

import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.platform.remoting.server.event.DolphinEventBus;

/**
 * The Dolphin Platform supports different implementations for the event bus (see {@link DolphinEventBus}). This is for example needed if the event bus should be used in a clustered or distributed environment. This intrerface provide a SPI that is loaded by the default Java SPI (see {@link java.util.ServiceLoader}) at runtime to provide a event bus implementation.
 *
 * @author Hendrik Ebbers
 */
public interface EventBusProvider {

    /**
     * Returns the unique type of the event bus that is provided by this instance
     * @return the unique type
     */
    String getType();

    /**
     * Returns the event bus.
     * @param configuration the configuration that can be used internally to create the event bus
     * @return the event bus
     */
    DolphinEventBus create(RemotingConfiguration configuration);

}
