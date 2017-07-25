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
import com.canoo.platform.server.spi.EventBusProvider;
import com.canoo.platform.server.event.DolphinEventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.ServiceLoader;

public class DistributedEventBusProvider implements EventBusProvider {

    private static final Logger LOG = LoggerFactory.getLogger(DistributedEventBusProvider.class);

    public static final String DISTRIBUTED_EVENTBUS_NAME = "distributed";

    @Override
    public String getType() {
        return DISTRIBUTED_EVENTBUS_NAME;
    }

    public DolphinEventBus create(final RemotingConfiguration configuration) {
        LOG.debug("creating distributed event bus");

        HazelcastProvider hazelcastProvider = null;
        Iterator<HazelcastProvider> iterator = ServiceLoader.load(HazelcastProvider.class).iterator();

        //TODO: configurable
        if(iterator.hasNext()) {
            hazelcastProvider = iterator.next();
        }
        if(iterator.hasNext()) {
            throw new IllegalStateException("More than one service implementation for found for " + HazelcastProvider.class);
        }

        if(hazelcastProvider == null) {
            hazelcastProvider = new DefaultHazelcastProvider();
        }

        LOG.debug("Using Hazelcast provider {}", hazelcastProvider.getClass());

        return new DistributedEventBus(hazelcastProvider.getHazelcastInstance(new HazelcastConfig(configuration.getConfiguration())));
    }

}
