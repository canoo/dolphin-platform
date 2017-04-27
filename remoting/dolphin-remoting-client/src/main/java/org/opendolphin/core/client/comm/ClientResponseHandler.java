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
package org.opendolphin.core.client.comm;

import org.opendolphin.RemotingConstants;
import org.opendolphin.core.Attribute;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.comm.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ClientResponseHandler {

    private static final Logger LOG = LoggerFactory.getLogger(ClientResponseHandler.class);

    private final ClientModelStore clientModelStore;

    public ClientResponseHandler(final ClientModelStore clientModelStore) {
        this.clientModelStore = Objects.requireNonNull(clientModelStore);
    }

    public void dispatchHandle(Command command) {
        if (command instanceof DeletePresentationModelCommand) {
            handleDeletePresentationModelCommand((DeletePresentationModelCommand) command);
        } else if (command instanceof CreatePresentationModelCommand) {
            handleCreatePresentationModelCommand((CreatePresentationModelCommand) command);
        } else if (command instanceof ValueChangedCommand) {
            handleValueChangedCommand((ValueChangedCommand) command);
        } else if (command instanceof QualifierChangedCommand) {
            handleQualifierChangedCommand((QualifierChangedCommand) command);
        } else {
            LOG.error("C: cannot handle unknown command '{}'", command);
        }
    }

    private void handleDeletePresentationModelCommand(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientModelStore.findPresentationModelById(serverCommand.getPmId());
        if (model == null) {
            throw new IllegalStateException("Can not find presentation model with id '" + serverCommand.getPmId());
        }
        clientModelStore.remove(model);
    }

    private void handleCreatePresentationModelCommand(CreatePresentationModelCommand serverCommand) {
        if (clientModelStore.containsPresentationModel(serverCommand.getPmId())) {
            throw new IllegalStateException("There already is a presentation model with id '" + serverCommand.getPmId() + "' known to the client.");
        }

        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map<String, Object> attr : serverCommand.getAttributes()) {
            Object propertyName = attr.get(RemotingConstants.PROPERTY_NAME);
            Object value = attr.get(RemotingConstants.VALUE_NAME);
            Object qualifier = attr.get(RemotingConstants.QUALIFIER_NAME);
            Object id = attr.get(RemotingConstants.ID_NAME);
            ClientAttribute attribute = new ClientAttribute(propertyName != null ? propertyName.toString() : null, value, qualifier != null ? qualifier.toString() : null);
            if (id != null && id.toString().endsWith(RemotingConstants.SERVER_ORIGIN)) {
                attribute.setId(id.toString());
            }
            attributes.add(attribute);
        }

        ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes);
        model.setPresentationModelType(serverCommand.getPmType());
        if (serverCommand.isClientSideOnly()) {
            model.setClientSideOnly(true);
        }
        clientModelStore.add(model);
        clientModelStore.updateQualifiers(model);
    }

    private void handleValueChangedCommand(ValueChangedCommand serverCommand) {
        Attribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            throw new IllegalStateException("Can not find attribute with id '" + serverCommand.getAttributeId());
        }

        if (attribute.getValue() == null && serverCommand.getNewValue() == null || (attribute.getValue() != null && serverCommand.getNewValue() != null && attribute.getValue().equals(serverCommand.getNewValue()))) {
            return;
        }
        LOG.info("C: updating '{}' id '{}' from '{}' to '{}' ", attribute.getPropertyName(), serverCommand.getAttributeId(), attribute.getValue(), serverCommand.getNewValue());
        attribute.setValue(serverCommand.getNewValue());
    }

    private void handleQualifierChangedCommand(QualifierChangedCommand serverCommand) {
        ClientAttribute attribute = clientModelStore.findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            throw new IllegalStateException("Can not find attribute with id '" + serverCommand.getAttributeId());
        }
        attribute.setQualifier(serverCommand.getQualifier());
    }
}
