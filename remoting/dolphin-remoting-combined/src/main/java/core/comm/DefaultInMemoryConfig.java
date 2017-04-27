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
package core.comm;

import core.client.comm.InMemoryClientConnector;
import org.opendolphin.core.client.ClientDolphin;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.DefaultModelSynchronizer;
import org.opendolphin.core.client.ModelSynchronizer;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.CommandBatcher;
import org.opendolphin.core.server.ServerDolphin;
import org.opendolphin.core.server.ServerDolphinFactory;
import org.opendolphin.util.Provider;

import java.util.concurrent.Executor;

/**
 * Base class for running a client and server dolphin inside the same VM.
 * <p>
 * Subclasses JavaFxInMemoryConfig and SwingInMemoryConfig additionally set the threading model
 * as appropriate for the UI (JavaFX or Swing, respectively.)
 */
@Deprecated
public class DefaultInMemoryConfig implements Provider<AbstractClientConnector> {

    private final ClientDolphin clientDolphin;

    private final ServerDolphin serverDolphin;

    private final InMemoryClientConnector clientConnector;

    public DefaultInMemoryConfig(final Executor uiExecutor) {
        clientDolphin = new ClientDolphin();
        serverDolphin = ServerDolphinFactory.create();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(this);
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        clientConnector = new InMemoryClientConnector(modelStore, serverDolphin.getServerConnector(), new CommandBatcher(), uiExecutor);
        clientConnector.connect(false);
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
