/*
 * Copyright 2015-2016 Canoo Engineering AG.
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
package com.canoo.remoting.combined;

import com.canoo.communication.common.LogConfig;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.server.ServerDolphin;
import com.canoo.remoting.server.ServerDolphinFactory;

import java.util.logging.Level;

/**
 * Base class for running a client and server dolphin inside the same VM.
 * <p>
 * Subclasses JavaFxInMemoryConfig and SwingInMemoryConfig additionally set the threading model
 * as appropriate for the UI (JavaFX or Swing, respectively.)
 */
public class DefaultInMemoryConfig {

    private ClientDolphin clientDolphin;

    private ServerDolphin serverDolphin;

    private final InMemoryClientConnector clientConnector;

    public DefaultInMemoryConfig() {
        LogConfig.logOnLevel(Level.INFO);

        clientDolphin = new ClientDolphin();
        serverDolphin = ServerDolphinFactory.create();

        clientDolphin.setClientModelStore(new ClientModelStore(clientDolphin));
        clientConnector = new InMemoryClientConnector(clientDolphin, serverDolphin.getServerConnector());
        clientDolphin.setClientConnector(clientConnector);

        clientConnector.setSleepMillis(100);

    }

    public ClientDolphin getClientDolphin() {
        return clientDolphin;
    }

    public ServerDolphin getServerDolphin() {
        return serverDolphin;
    }

    public InMemoryClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientDolphin(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
    }

    public void setServerDolphin(ServerDolphin serverDolphin) {
        this.serverDolphin = serverDolphin;
    }
}
