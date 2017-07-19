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
package org.opendolphin.core.client;

import org.opendolphin.core.Dolphin;
import org.opendolphin.core.client.comm.AbstractClientConnector;
import org.opendolphin.core.client.comm.OnFinishedHandler;
import org.opendolphin.core.comm.EmptyCommand;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
@Deprecated
public class ClientDolphin implements Dolphin<ClientAttribute, ClientPresentationModel> {

    private ClientModelStore clientModelStore;

    private AbstractClientConnector clientConnector;

    @Override
    public ClientModelStore getModelStore() {
        return clientModelStore;
    }

    public AbstractClientConnector getClientConnector() {
        return clientConnector;
    }

    /**
     * both java- and groovy-friendly convenience method to send an empty command, which will have no
     * presentation models nor data in the callback
     */
    @Deprecated
    public void sync(final Runnable runnable) {
        clientConnector.send(new EmptyCommand(), new OnFinishedHandler() {
            public void onFinished() {
                runnable.run();
            }

        });
    }

    /**
     * @deprecated Model store should be final and defined by constructor
     * @param clientModelStore
     */
    @Deprecated
    public void setClientModelStore(final ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    /**
     * @deprecated client connector should be final and defined by constructor
     * @param clientConnector
     */
    @Deprecated
    public void setClientConnector(final AbstractClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

}
