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
package com.canoo.remoting.client;

import com.canoo.communication.common.ModelStore;
import com.canoo.communication.common.ModelStoreConfig;
import com.canoo.remoting.client.communication.AttributeChangeListener;
import com.canoo.remoting.client.communication.ClientConnector;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;
import com.canoo.communication.common.commands.DeletedPresentationModelNotification;

import java.util.List;

/**
 * The ClientModelStore is a {@link ModelStore} with customized behavior appropriate to the client
 * (view) side of a Dolphin connection.  It connects the model store with the {@link ClientDolphin} via
 * an {@link AttributeChangeListener}.  It automatically notifies the server side when presentation models are added
 * or removed.
 */
public class ClientModelStore extends ModelStore<ClientAttribute, ClientPresentationModel> {

    private final ClientDolphin clientDolphin;

    protected final AttributeChangeListener attributeChangeListener;

    /**
     * Constructs a client model store with default capacities.
     * @see ModelStoreConfig
     */
    public ClientModelStore(ClientDolphin clientDolphin) {
        this(clientDolphin, new ModelStoreConfig());
    }

    public ClientModelStore(ClientDolphin clientDolphin, ModelStoreConfig config) {
        super(config);
        this.clientDolphin = clientDolphin;
        attributeChangeListener = new AttributeChangeListener();
        attributeChangeListener.setClientConnector(getClientConnector());
        attributeChangeListener.setClientModelStore(this);
    }

    protected ClientConnector getClientConnector() {
        ClientConnector clientConnector = clientDolphin.getClientConnector();
        if (null == attributeChangeListener.getClientConnector()){
            attributeChangeListener.setClientConnector(clientConnector);
        }
        return clientConnector;
    }

    // ModelStoreListener ADDED will be fired before server is notified.
    @Override
    public boolean add(ClientPresentationModel model) {
        boolean success = super.add(model);
        if (success) {
            List<ClientAttribute> attributes = model.getAttributes();
            for (ClientAttribute attribute : attributes) {
                attribute.addPropertyChangeListener(attributeChangeListener);
            }
            if (!model.isClientSideOnly()) {
                getClientConnector().send(CreatePresentationModelCommand.makeFrom(model));
            }
        }
        return success;
    }

    @Override
    public boolean remove(ClientPresentationModel model) {
        boolean success = super.remove(model);
        for (ClientAttribute attribute : model.getAttributes()) {
            attribute.removePropertyChangeListener(attributeChangeListener);
        }
        return success;
    }

    @Override
    public void registerAttribute(ClientAttribute attribute) {
        super.registerAttribute(attribute);
        attribute.addPropertyChangeListener(attributeChangeListener);
    }

    public void delete(ClientPresentationModel model) {
        delete(model, true);
    }

    public void delete(ClientPresentationModel model, boolean notify) {
        if (model == null) return;
        if (containsPresentationModel(model.getId())) {
            remove(model);
            if (!notify) return;
            if (model.isClientSideOnly()) return;
            getClientConnector().send(new DeletedPresentationModelNotification(model.getId()));
        }
    }
}
