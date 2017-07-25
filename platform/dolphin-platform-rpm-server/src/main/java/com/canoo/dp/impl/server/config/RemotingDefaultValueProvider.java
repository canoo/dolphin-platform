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

import com.canoo.platform.server.spi.ConfigurationProviderAdapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RemotingDefaultValueProvider extends ConfigurationProviderAdapter {

    @Override
    public Map<String, String> getStringProperties() {
        HashMap<String, String> ret = new HashMap<>();

        ret.put(RemotingConfiguration.DOLPHIN_PLATFORM_SERVLET_MAPPING, RemotingConfiguration.DOLPHIN_PLATFORM_SERVLET_MAPPING_DEFAULT_VALUE);
        ret.put(RemotingConfiguration.DOLPHIN_PLATFORM_INTERRUPT_SERVLET_MAPPING, RemotingConfiguration.DOLPHIN_PLATFORM_INTERRUPT_SERVLET_MAPPING_DEFAULT_VALUE);
        ret.put(RemotingConfiguration.EVENTBUS_TYPE, RemotingConfiguration.EVENTBUS_TYPE_DEFAULT_VALUE);
        return ret;
    }

    @Override
    public Map<String, Long> getLongProperties() {
        return Collections.singletonMap(RemotingConfiguration.MAX_POLL_TIME, RemotingConfiguration.MAX_POLL_TIME_DEFAULT_VALUE);
    }

    @Override
    public Map<String, Boolean> getBooleanProperties() {
        return Collections.singletonMap(RemotingConfiguration.GARBAGE_COLLECTION_ACTIVE, RemotingConfiguration.USE_GC_DEFAULT_VALUE);
    }
}
