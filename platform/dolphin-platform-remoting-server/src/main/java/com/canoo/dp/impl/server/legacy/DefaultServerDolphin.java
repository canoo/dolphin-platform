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
package com.canoo.dp.impl.server.legacy;

/**
 * The default implementation of the Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */
@Deprecated
public class DefaultServerDolphin implements ServerDolphin {

    /**
     * the server model store is unique per user session
     */
    private final ServerModelStore serverModelStore;

    /**
     * the serverConnector is unique per user session
     */
    private final ServerConnector serverConnector;


    public DefaultServerDolphin(final ServerModelStore serverModelStore, final ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore;
        this.serverConnector = serverConnector;
        this.serverConnector.setServerModelStore(serverModelStore);
    }

    @Override
    public ServerModelStore getModelStore() {
        return serverModelStore;
    }

    @Override
    public ServerConnector getServerConnector() {
        return serverConnector;
    }

}
