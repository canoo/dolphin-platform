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
package com.canoo.impl.server.util;

import com.canoo.dp.impl.client.legacy.ClientDolphin;
import com.canoo.dp.impl.client.legacy.ClientModelStore;
import com.canoo.dp.impl.client.legacy.ModelSynchronizer;
import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.client.legacy.communication.CommandBatcher;
import com.canoo.dp.impl.server.legacy.DefaultServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerConnector;
import com.canoo.dp.impl.server.legacy.ServerDolphin;
import com.canoo.dp.impl.server.legacy.ServerModelStore;
import com.canoo.platform.core.functional.Provider;

import java.util.concurrent.Executor;

/**
 * Base class for running a client and server dolphin inside the same VM.
 * <p>
 * Subclasses JavaFxInMemoryConfig and SwingInMemoryConfig additionally set the threading model
 * as appropriate for the UI (JavaFX or Swing, respectively.)
 */
public class DefaultInMemoryConfig implements Provider<AbstractClientConnector> {

    private final ClientDolphin clientDolphin;

    private final ServerDolphin serverDolphin;

    private final InMemoryClientConnector clientConnector;

    public DefaultInMemoryConfig(final Executor uiExecutor) {
        clientDolphin = new ClientDolphin();
        serverDolphin = new DefaultServerDolphin(new ServerModelStore(), new ServerConnector());
        ModelSynchronizer defaultModelSynchronizer = new ModelSynchronizer(this);
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        clientConnector = new InMemoryClientConnector(modelStore, serverDolphin.getServerConnector(), new CommandBatcher(), uiExecutor);
        clientDolphin.setClientModelStore(modelStore);
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

    @Override
    public AbstractClientConnector get() {
        return clientConnector;
    }
}
