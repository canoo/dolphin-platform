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
package org.opendolphin.core.server;

/**
 * A factory class to create a ServerDolphin object.
 */
@Deprecated
public class ServerDolphinFactory {

    private ServerDolphinFactory() {}

    /**
     * Creates a default ServerDolphin object containing a default ServerModelStore and ServerConnector.
     */
    public static ServerDolphin create() {
        return new DefaultServerDolphin();
    }

    /**
     * Creates a default ServerDolphin object using the supplied model store and server connector.
     * @param serverModelStore
     * @param serverConnector
     * @return
     */
    public static ServerDolphin create(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        return new DefaultServerDolphin(serverModelStore, serverConnector);
    }
}
