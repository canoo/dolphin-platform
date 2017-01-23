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

import com.canoo.communication.common.AbstractDolphin;
import com.canoo.communication.common.ModelStore;
import com.canoo.remoting.client.communication.ClientConnector;
import com.canoo.remoting.client.communication.OnFinishedHandler;
import com.canoo.communication.common.commands.AttributeCreatedNotification;
import com.canoo.communication.common.commands.EmptyNotification;
import com.canoo.communication.common.commands.NamedCommand;
import com.canoo.communication.common.commands.SignalCommand;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The main Dolphin facade on the client side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with client model store and client connector.
 * Threading model: confined to the UI handling thread.
 */
public class ClientDolphin extends AbstractDolphin<ClientAttribute, ClientPresentationModel> {

    private ClientModelStore clientModelStore;

    private ClientConnector clientConnector;

    @Override
    public ModelStore getModelStore() {
        return clientModelStore;
    }

    /**
     * Convenience method for a creating a ClientPresentationModel with initial null values for the attributes
     */
    public ClientPresentationModel presentationModel(String id, List<String> attributeNames) {
        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (String name : attributeNames) {
            attributes.add(new ClientAttribute(name, null));
        }

        ClientPresentationModel result = new ClientPresentationModel(id, attributes);
        clientModelStore.add(result);
        return result;
    }

    public ClientPresentationModel presentationModel(String id, Map<String, Object> attributeNamesAndValues) {
        return presentationModel(id, null, attributeNamesAndValues);
    }

    /**
     * groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values
     */
    public ClientPresentationModel presentationModel(String id, String presentationModelType, Map<String, Object> attributeNamesAndValues) {
        return presentationModel(attributeNamesAndValues, id, presentationModelType);
    }

    public ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id) {
        return presentationModel(attributeNamesAndValues, id, null);
    }

    /**
     * groovy-friendly convenience method for a typical case of creating a ClientPresentationModel with initial values
     */
    public ClientPresentationModel presentationModel(Map<String, Object> attributeNamesAndValues, String id, String presentationModelType) {
        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map.Entry<String, Object> entry : attributeNamesAndValues.entrySet()) {
            ((ArrayList<ClientAttribute>) attributes).add(new ClientAttribute(entry.getKey(), entry.getValue()));
        }

        ClientPresentationModel result = new ClientPresentationModel(id, attributes);
        result.setPresentationModelType(presentationModelType);
        clientModelStore.add(result);
        return result;
    }

    public ClientPresentationModel presentationModel(String id, ClientAttribute... attributes) {
        return presentationModel(id, null, attributes);
    }

    /**
     * both groovy- and java-friendly full-control constructor
     */
    public ClientPresentationModel presentationModel(String id, String presentationModelType, ClientAttribute... attributes) {
        ClientPresentationModel result = new ClientPresentationModel(id, Arrays.asList(attributes));
        result.setPresentationModelType(presentationModelType);
        clientModelStore.add(result);
        return result;
    }

    /**
     * java-friendly convenience method for sending a named command
     */
    public void send(String commandName, OnFinishedHandler onFinished) {
        clientConnector.send(new NamedCommand(commandName), onFinished);
    }

    public void send(String commandName) {
        clientConnector.send(new NamedCommand(commandName), null);
    }

    /**
     * both java- and groovy-friendly convenience method to send an empty command, which will have no
     * presentation models nor data in the callback
     */
    public void sync(final Runnable runnable) {
        clientConnector.send(new EmptyNotification(), new OnFinishedHandler() {
            public void onFinished(List<ClientPresentationModel> presentationModels) {
                runnable.run();
            }

        });
    }

    /**
     * Removes the modelToDelete from the client model store,
     * detaches all model store listeners,
     * and notifies the server if successful
     */
    public void delete(ClientPresentationModel modelToDelete) {
        clientModelStore.delete(modelToDelete);
    }

    /**
     * Adds the supplied attribute to the model store for the specified presentation model.
     *
     * @param presentationModel
     * @param attribute
     */
    public void addAttributeToModel(ClientPresentationModel presentationModel, ClientAttribute attribute) {
        presentationModel._internal_addAttribute(attribute);
        clientModelStore.registerAttribute(attribute);
        if (!presentationModel.isClientSideOnly()) {
            clientConnector.send(new AttributeCreatedNotification(presentationModel.getId(), attribute.getId(), attribute.getPropertyName(), attribute.getValue(), attribute.getQualifier()));
        }

    }

    public void startPushListening(String pushActionName, String releaseActionName) {
        if (pushActionName == null) {
            return;

        }

        if (releaseActionName == null) {
            return;

        }

        clientConnector.setPushListener(new NamedCommand(pushActionName));
        clientConnector.setReleaseCommand(new SignalCommand(releaseActionName));
        clientConnector.setPushEnabled(true);
        clientConnector.listen();
    }

    public void stopPushListening() {
        clientConnector.setPushEnabled(false);
    }

    public boolean isPushListening() {
        return clientConnector.isPushEnabled();
    }

    public ClientModelStore getClientModelStore() {
        return clientModelStore;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientModelStore(ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }

}
