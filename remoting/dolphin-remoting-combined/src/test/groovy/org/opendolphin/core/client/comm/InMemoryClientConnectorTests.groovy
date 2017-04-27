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
package org.opendolphin.core.client.comm

import core.client.comm.InMemoryClientConnector
import org.opendolphin.core.client.ClientDolphin
import org.opendolphin.core.client.ClientModelStore
import org.opendolphin.core.client.DefaultModelSynchronizer
import org.opendolphin.core.client.ModelSynchronizer
import org.opendolphin.core.comm.EmptyNotification
import org.opendolphin.core.server.ServerConnector
import org.opendolphin.util.DirectExecutor
import org.opendolphin.util.Provider

class InMemoryClientConnectorTests extends GroovyTestCase {

    void testCallConnector_NoServerConnectorWired() {

        def serverConnector = [receive: { cmd ->
            return []
        }] as ServerConnector

        ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }
        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        AbstractClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());
        connector.connect(false);
        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);

        assert [] == connector.transmit([new EmptyNotification()])
    }

    void testCallConnector_ServerWired() {
        boolean serverCalled = false
        def serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }] as ServerConnector

        ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }
        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        AbstractClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());
        connector.connect(false);

        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);

        def command = new EmptyNotification()
        connector.transmit([command])
        assert serverCalled
    }

    void testCallConnector_ServerWiredWithSleep() {
        boolean serverCalled = false
        def serverConnector = [receive: { cmd ->
            serverCalled = true
            return []
        }] as ServerConnector

        ClientDolphin clientDolphin = new ClientDolphin();
        ModelSynchronizer defaultModelSynchronizer = new DefaultModelSynchronizer(new Provider<AbstractClientConnector>() {
            @Override
            AbstractClientConnector get() {
                return clientDolphin.getClientConnector();
            }
        });
        ClientModelStore modelStore = new ClientModelStore(defaultModelSynchronizer);
        AbstractClientConnector connector = new InMemoryClientConnector(modelStore, serverConnector, new CommandBatcher(), DirectExecutor.getInstance());
        connector.connect(false);

        clientDolphin.setClientConnector(connector);
        clientDolphin.setClientModelStore(modelStore);

        connector.sleepMillis = 10
        def command = new EmptyNotification()
        connector.transmit([command])
        assert serverCalled
    }

}
