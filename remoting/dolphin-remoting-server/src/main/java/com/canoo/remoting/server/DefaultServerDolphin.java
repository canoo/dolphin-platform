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
package com.canoo.remoting.server;

import com.canoo.common.StringUtil;
import com.canoo.communication.common.AbstractDolphin;
import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;
import com.canoo.communication.common.commands.DeletePresentationModelCommand;
import com.canoo.communication.common.commands.InitializeAttributeCommand;
import com.canoo.communication.common.commands.ValueChangedCommand;
import com.canoo.remoting.server.action.CreatePresentationModelAction;
import com.canoo.remoting.server.action.DeletePresentationModelAction;
import com.canoo.remoting.server.action.DolphinServerAction;
import com.canoo.remoting.server.action.EmptyAction;
import com.canoo.remoting.server.action.StoreAttributeAction;
import com.canoo.remoting.server.action.StoreValueChangeAction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * The default implementation of the Dolphin facade on the server side.
 * Responsibility: single access point for dolphin capabilities.
 * Collaborates with server model store and current response.
 * Threading model: confined to a single controller thread.
 */
public class DefaultServerDolphin extends AbstractDolphin<ServerAttribute, ServerPresentationModel> implements ServerDolphin {

    private static final Logger LOG = Logger.getLogger(DefaultServerDolphin.class.getName());

    /**
     * the server model store is unique per user session
     */
    private final ServerModelStore serverModelStore;

    /**
     * the serverConnector is unique per user session
     */
    private final ServerConnector serverConnector;

    private AtomicBoolean initialized = new AtomicBoolean(false);

    public DefaultServerDolphin(ServerModelStore serverModelStore, ServerConnector serverConnector) {
        this.serverModelStore = serverModelStore;
        this.serverConnector = serverConnector;
        this.serverConnector.setServerModelStore(serverModelStore);
    }

    protected DefaultServerDolphin() {
        this(new ServerModelStore(), new ServerConnector());
    }

    @Override
    public ServerModelStore getModelStore() {
        return serverModelStore;
    }

    @Override
    public ServerConnector getServerConnector() {
        return serverConnector;
    }

    public void registerDefaultActions() {
        if (initialized.getAndSet(true)) {
            LOG.warning("attempt to initialize default actions more than once!");
            return;
        }
        register(new StoreValueChangeAction());
        register(new StoreAttributeAction());
        register(new CreatePresentationModelAction());
        register(new DeletePresentationModelAction());
        serverConnector.register(new EmptyAction());
    }

    public void register(DolphinServerAction action) {
        action.setServerDolphin(this);
        serverConnector.register(action);
    }

    /**
     * Adding the model to the model store and if successful, sending the CreatePresentationModelCommand.
     *
     * @param model the model to be added.
     * @return whether the adding was successful, which implies that also the command has been sent
     */
    @Override
    public boolean addPresentationModel(ServerPresentationModel model) {
        boolean result = super.addPresentationModel(model);
        if (result) {
            serverModelStore.getCurrentResponse().add(CreatePresentationModelCommand.makeFrom(model));
        }
        return result;
    }

    /**
     * Create a presentation model on the server side, add it to the model store, and send a command to
     * the client, advising him to do the same.
     *
     * @throws IllegalArgumentException if a presentation model for this id already exists. No commands are sent in this case.
     */
    public ServerPresentationModel presentationModel(String id, String presentationModelType, DTO dto) {
        List<ServerAttribute> attributes = new ArrayList<ServerAttribute>();
        for (final Slot slot : dto.getSlots()) {
            final ServerAttribute result = new ServerAttribute(slot.getPropertyName(), slot.getValue(), slot.getQualifier());
            result.silently(new Runnable() {
                @Override
                public void run() {
                    result.setValue(slot.getValue());
                }

            });
            ((ArrayList<ServerAttribute>) attributes).add(result);
        }
        ServerPresentationModel model = new ServerPresentationModel(id, attributes, serverModelStore);
        model.setPresentationModelType(presentationModelType);
        addPresentationModel(model);
        return model;
    }

    /**
     * Convenience method to let the client (!) dolphin create a presentation model as specified by the DTO.
     * The server model store remains untouched until the client has issued the notification.
     */
    public static void presentationModelCommand(List<Command> response, String id, String presentationModelType, DTO dto) {
        if (response == null) {
            return;
        }
        List<Map<String, Object>> list = new ArrayList<>();
        for (Slot slot : dto.getSlots()) {
            Map<String, Object> map = new HashMap<>();
            map.put("propertyName", slot.getPropertyName());
            map.put("value", slot.getValue());
            map.put("qualifier", slot.getQualifier());
            list.add(map);
        }
        response.add(new CreatePresentationModelCommand(id, presentationModelType, list));
    }

    /**
     * Convenience method to let Dolphin removePresentationModel a presentation model directly on the server and notify the client.
     */
    public boolean removePresentationModel(ServerPresentationModel pm) {
        boolean deleted = serverModelStore.remove(pm);
        if (deleted) {
            DefaultServerDolphin.deleteCommand(serverModelStore.getCurrentResponse(), pm.getId());
        }
        return deleted;
    }

    /**
     * Convenience method to let Dolphin delete a presentation model on the client side
     */
    public static void deleteCommand(List<Command> response, String pmId) {
        if (response == null || StringUtil.isBlank(pmId)) {
            return;
        }
        response.add(new DeletePresentationModelCommand(pmId));
    }

    /**
     * Convenience method to change an attribute value on the server side.
     *
     * @param response  must not be null or the method silently ignores the call
     * @param attribute must not be null
     */
    public static void changeValueCommand(List<Command> response, ServerAttribute attribute, Object value) {
        if (response == null) {
            return;
        }
        if (attribute == null) {
            LOG.severe("Cannot change value on a null attribute to '" + value);
            return;
        }
        forceChangeValue(value, response, attribute);
    }

    /**
     * @deprecated use forceChangeValueCommand(Object, List, ServerAttribute). You can use the "inline method refactoring". Will be removed in version 1.0!
     */
    public static void forceChangeValue(Object value, List<Command> response, ServerAttribute attribute) {
        response.add(new ValueChangedCommand(attribute.getId(), attribute.getValue(), value));
    }

    /**
     * Convenience method for the InitializeAttributeCommand
     */
    public static void initAt(List<Command> response, String pmId, String propertyName, String qualifier, Object newValue) {
        if (null == response) {
            return;
        }
        response.add(new InitializeAttributeCommand(pmId, propertyName, qualifier, newValue));
    }

    public ServerModelStore getServerModelStore() {
        return serverModelStore;
    }
}
