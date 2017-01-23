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
package com.canoo.remoting.client.communication;

import com.canoo.communication.common.Attribute;
import com.canoo.remoting.client.ClientAttribute;
import com.canoo.remoting.client.ClientDolphin;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.client.ClientPresentationModel;
import com.canoo.communication.common.commands.AttributeMetadataChangedCommand;
import com.canoo.communication.common.commands.CallNamedActionCommand;
import com.canoo.communication.common.commands.Command;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;
import com.canoo.communication.common.commands.DataCommand;
import com.canoo.communication.common.commands.DeletePresentationModelCommand;
import com.canoo.communication.common.commands.InitializeAttributeCommand;
import com.canoo.communication.common.commands.ValueChangedCommand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class ClientResponseHandler {
    public ClientResponseHandler(ClientDolphin clientDolphin) {
        this.clientDolphin = clientDolphin;
    }

    protected ClientModelStore getClientModelStore() {
        return clientDolphin.getClientModelStore();
    }

    public Object dispatchHandle(Command command) {
        if (command instanceof DataCommand) {
            return handleDataCommand((DataCommand) command);
        } else if (command instanceof DeletePresentationModelCommand) {
            return handleDeletePresentationModelCommand((DeletePresentationModelCommand) command);
        } else if (command instanceof CreatePresentationModelCommand) {
            return handleCreatePresentationModelCommand((CreatePresentationModelCommand) command);
        } else if (command instanceof ValueChangedCommand) {
            return handleValueChangedCommand((ValueChangedCommand) command);
        } else if (command instanceof InitializeAttributeCommand) {
            return handleInitializeAttributeCommand((InitializeAttributeCommand) command);
        } else if (command instanceof AttributeMetadataChangedCommand) {
            return handleAttributeMetadataChangedCommand((AttributeMetadataChangedCommand) command);
        } else if (command instanceof CallNamedActionCommand) {
            return handleCallNamedActionCommand((CallNamedActionCommand) command);
        } else {
            handleUnknownCommand(command);
            return null;
        }

    }

    private void handleUnknownCommand(Command serverCommand) {
        LOG.severe("C: cannot handle unknown command '" + serverCommand + "'");
    }

    private Map handleDataCommand(DataCommand serverCommand) {
        return serverCommand.getData();
    }

    private ClientPresentationModel handleDeletePresentationModelCommand(DeletePresentationModelCommand serverCommand) {
        ClientPresentationModel model = clientDolphin.getPresentationModel(serverCommand.getPmId());
        if (model == null) {
            return null;
        }

        getClientModelStore().delete(model);
        return model;
    }

    private ClientPresentationModel handleCreatePresentationModelCommand(CreatePresentationModelCommand serverCommand) {
        if (getClientModelStore().containsPresentationModel(serverCommand.getPmId())) {
            throw new IllegalStateException("There already is a presentation model with id '" + serverCommand.getPmId() + "' known to the client.");
        }

        List<ClientAttribute> attributes = new ArrayList<ClientAttribute>();
        for (Map<String, Object> attr : serverCommand.getAttributes()) {

            Object propertyName = attr.get("propertyName");
            Object value = attr.get("value");
            Object qualifier = attr.get("qualifier");
            Object id = attr.get("id");


            ClientAttribute attribute = new ClientAttribute(propertyName != null ? propertyName.toString() : null, value, qualifier != null ? qualifier.toString() : null);
            if (id != null && id.toString().endsWith("S")) {
                attribute.setId(id.toString());
            }


            ((ArrayList<ClientAttribute>) attributes).add(attribute);
        }

        ClientPresentationModel model = new ClientPresentationModel(serverCommand.getPmId(), attributes);
        model.setPresentationModelType(serverCommand.getPmType());
        if (serverCommand.isClientSideOnly()) {
            model.setClientSideOnly(true);
        }

        getClientModelStore().add(model);
        clientDolphin.updateQualifiers(model);
        return model;
    }

    private ClientPresentationModel handleValueChangedCommand(ValueChangedCommand serverCommand) {
        Attribute attribute = getClientModelStore().findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            LOG.warning("C: attribute with id '" + serverCommand.getAttributeId() + "' not found, cannot update old value '" + serverCommand.getOldValue() + "' to new value '" + serverCommand.getNewValue() + "'");
            return null;
        }

        if (((ClientAttribute) attribute).getValue() == null && serverCommand.getNewValue() == null || (((ClientAttribute) attribute).getValue() != null && serverCommand.getNewValue() != null && ((ClientAttribute) attribute).getValue().equals(serverCommand.getNewValue()))) {
            return null;
        }


        if (strictMode && ((((ClientAttribute) attribute).getValue() == null && serverCommand.getOldValue() != null) || (((ClientAttribute) attribute).getValue() != null && serverCommand.getOldValue() == null) || (((ClientAttribute) attribute).getValue() != null && !((ClientAttribute) attribute).getValue().equals(serverCommand.getOldValue())))) {
            // todo dk: think about sending a RejectCommand here to tell the server about a possible lost update
            LOG.warning("C: attribute with id '" + serverCommand.getAttributeId() + "' and value '" + ((ClientAttribute) attribute).getValue() + "' cannot be set to new value '" + serverCommand.getNewValue() + "' because the change was based on an outdated old value of '" + serverCommand.getOldValue() + "'.");
            return null;
        }

        LOG.info("C: updating '" + ((ClientAttribute) attribute).getPropertyName() + "' id '" + serverCommand.getAttributeId() + "' from '" + ((ClientAttribute) attribute).getValue() + "' to '" + serverCommand.getNewValue() + "'");
        ((ClientAttribute) attribute).setValue(serverCommand.getNewValue());
        return null;// this command is not expected to be sent explicitly, so no pm needs to be returned
    }

    private ClientPresentationModel handleInitializeAttributeCommand(InitializeAttributeCommand serverCommand) {
        ClientAttribute attribute = new ClientAttribute(serverCommand.getPropertyName(), serverCommand.getNewValue(), serverCommand.getQualifier());

        // todo: add check for no-value; null is a valid value
        if (serverCommand.getQualifier() != null) {
            List<ClientAttribute> copies = getClientModelStore().findAllAttributesByQualifier(serverCommand.getQualifier());
            if (copies != null && !copies.isEmpty()) {
                if (serverCommand.getNewValue() == null) {
                    attribute.setValue(copies.get(0).getValue());
                } else {
                    for (ClientAttribute attr : copies) {
                        attr.setValue(attribute.getValue());
                    }

                }

            }

        }

        ClientPresentationModel presentationModel = null;
        if (serverCommand.getPmId() != null) {
            presentationModel = getClientModelStore().findPresentationModelById(serverCommand.getPmId());
        }

        // here we could have a pmType conflict and we may want to throw an Exception...
        // if there is no pmId, it is most likely an error and CreatePresentationModelCommand should have been used
        if (presentationModel == null) {
            presentationModel = new ClientPresentationModel(serverCommand.getPmId(), Collections.<ClientAttribute>emptyList());
            presentationModel.setPresentationModelType(serverCommand.getPmType());
            getClientModelStore().add(presentationModel);
        }

        // if we already have the attribute, just update the value
        Attribute existingAtt = presentationModel.getAttribute(serverCommand.getPropertyName());
        if (existingAtt != null) {
            ((ClientAttribute) existingAtt).setValue(attribute.getValue());
        } else {
            clientDolphin.addAttributeToModel(presentationModel, attribute);
        }

        clientDolphin.updateQualifiers(presentationModel);
        return presentationModel;// todo dk: check and test
    }

    private ClientPresentationModel handleAttributeMetadataChangedCommand(AttributeMetadataChangedCommand serverCommand) {
        ClientAttribute attribute = getClientModelStore().findAttributeById(serverCommand.getAttributeId());
        if (attribute == null) {
            return null;
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.VALUE_NAME)) {
            attribute.setValue(serverCommand.getValue());
        }

        if (serverCommand.getMetadataName() != null && serverCommand.getMetadataName().equals(Attribute.QUALIFIER_NAME)) {
            if(serverCommand.getValue() == null) {
                attribute.setQualifier(null);
            } else {
                attribute.setQualifier(serverCommand.getValue().toString());
            }
        }

        return null;
    }

    private ClientPresentationModel handleCallNamedActionCommand(CallNamedActionCommand serverCommand) {
        clientDolphin.send(serverCommand.getActionName());
        return null;
    }

    public boolean getStrictMode() {
        return strictMode;
    }

    public void setStrictMode(boolean strictMode) {
        this.strictMode = strictMode;
    }

    private static final Logger LOG = Logger.getLogger(ClientResponseHandler.class.getName());
    private final ClientDolphin clientDolphin;
    private boolean strictMode = true;
}
