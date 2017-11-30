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
package com.canoo.dp.impl.server.event;

import com.canoo.platform.server.spi.ConfigurationProvider;
import org.apiguardian.api.API;

import java.util.HashMap;
import java.util.Map;

import static org.apiguardian.api.API.Status.INTERNAL;

@API(since = "0.x", status = INTERNAL)
public class DistributedEventBusConfigProvider implements ConfigurationProvider {

    public static final String HAZELCAST_SERVER_NAME = "hazelcast.server.name";

    public static final String HAZELCAST_SERVER_PORT = "hazelcast.server.port";

    public static final String HAZELCAST_GROUP_NAME = "hazelcast.group.name";

    public static final String HAZELCAST_CONNECTION_ATTEMPT_COUNT = "hazelcast.connection.attempt.count";

    public static final String HAZELCAST_CONNECTION_ATTEMPT_PERIOD = "hazelcast.connection.attempt.period";

    public static final String HAZELCAST_CONNECTION_TIMEOUT = "hazelcast.connection.timeout";


    public static final String DEFAULT_HAZELCAST_SERVER = "localhost";

    public static final String DEFAULT_HAZELCAST_PORT = "5701";

    public static final String DEFAULT_HAZELCAST_GROUP_NAME = "micro-landscape";

    public static final int DEFAULT_HAZELCAST_CONNECTION_ATTEMPT_COUNT = 2;

    public static final int DEFAULT_HAZELCAST_CONNECTION_ATTEMPT_PERIOD = 3000;

    public static final int DEFAULT_HAZELCAST_CONNECTION_TIMEOUT = 5000;

    @Override
    public Map<String, String> getStringProperties() {
        Map<String, String> properties = new HashMap<>();
        properties.put(HAZELCAST_SERVER_NAME, DEFAULT_HAZELCAST_SERVER);
        properties.put(HAZELCAST_SERVER_PORT, DEFAULT_HAZELCAST_PORT);
        properties.put(HAZELCAST_GROUP_NAME, DEFAULT_HAZELCAST_GROUP_NAME);
        return properties;
    }

    @Override
    public Map<String, Integer> getIntegerProperties() {
        Map<String, Integer> properties = new HashMap<>();
        properties.put(HAZELCAST_CONNECTION_ATTEMPT_COUNT, DEFAULT_HAZELCAST_CONNECTION_ATTEMPT_COUNT);
        properties.put(HAZELCAST_CONNECTION_ATTEMPT_PERIOD, DEFAULT_HAZELCAST_CONNECTION_ATTEMPT_PERIOD);
        properties.put(HAZELCAST_CONNECTION_TIMEOUT, DEFAULT_HAZELCAST_CONNECTION_TIMEOUT);
        return properties;
    }
}
