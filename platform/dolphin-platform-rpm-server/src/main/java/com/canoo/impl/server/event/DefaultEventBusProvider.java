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
package com.canoo.impl.server.event;

import com.canoo.dp.impl.server.config.RemotingConfiguration;
import com.canoo.platform.server.event.DolphinEventBus;
import com.canoo.platform.server.spi.EventBusProvider;

public class DefaultEventBusProvider implements EventBusProvider {

    public static final String DEFAULT_EVENTBUS_NAME = "default";

    @Override
    public String getType() {
        return DEFAULT_EVENTBUS_NAME;
    }

    @Override
    public DolphinEventBus create(RemotingConfiguration configuration) {
        return new DefaultDolphinEventBus();
    }
}
