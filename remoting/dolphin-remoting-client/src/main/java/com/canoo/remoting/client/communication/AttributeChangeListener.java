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
import com.canoo.communication.common.PresentationModel;
import com.canoo.remoting.client.ClientAttribute;
import com.canoo.remoting.client.ClientModelStore;
import com.canoo.remoting.client.ClientPresentationModel;
import com.canoo.communication.common.commands.ChangeAttributeMetadataCommand;
import com.canoo.communication.common.commands.ValueChangedCommand;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class AttributeChangeListener implements PropertyChangeListener {

    private ClientModelStore clientModelStore;

    private ClientConnector clientConnector;

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Attribute.VALUE_NAME)) {
            if (evt.getOldValue() == null && evt.getNewValue() == null || evt.getOldValue() != null && evt.getNewValue() != null && evt.getOldValue().equals(evt.getNewValue())) {
                return;
            }

            if (isSendable(evt)) {
                clientConnector.send(constructValueChangedCommand(evt));
            }

            List<ClientAttribute> attributes = clientModelStore.findAllAttributesByQualifier(((Attribute) evt.getSource()).getQualifier());
            for (ClientAttribute attribute : attributes) {
                attribute.setValue(evt.getNewValue());
            }

        } else {
            // we assume the change is on a metadata property such as qualifier
            if (isSendable(evt)) {
                clientConnector.send(constructChangeAttributeMetadataCommand(evt));
            }
        }
    }

    private boolean isSendable(PropertyChangeEvent evt) {
        PresentationModel pmOfAttribute = ((Attribute) evt.getSource()).getPresentationModel();
        if (pmOfAttribute == null) {
            return true;
        }

        if (pmOfAttribute instanceof ClientPresentationModel && ((ClientPresentationModel) pmOfAttribute).isClientSideOnly()) {
            return false;
        }

        return true;
    }

    private ValueChangedCommand constructValueChangedCommand(PropertyChangeEvent evt) {
        return new ValueChangedCommand(((Attribute) evt.getSource()).getId(), evt.getOldValue(), evt.getNewValue());
    }

    private ChangeAttributeMetadataCommand constructChangeAttributeMetadataCommand(PropertyChangeEvent evt) {
        return new ChangeAttributeMetadataCommand(((Attribute) evt.getSource()).getId(), evt.getPropertyName(), evt.getNewValue());
    }

    public void setClientModelStore(ClientModelStore clientModelStore) {
        this.clientModelStore = clientModelStore;
    }

    public ClientConnector getClientConnector() {
        return clientConnector;
    }

    public void setClientConnector(ClientConnector clientConnector) {
        this.clientConnector = clientConnector;
    }
}
