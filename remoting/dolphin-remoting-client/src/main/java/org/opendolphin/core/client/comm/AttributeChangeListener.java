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
import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.client.ClientAttribute;
import org.opendolphin.core.client.ClientModelStore;
import org.opendolphin.core.client.ClientPresentationModel;
import org.opendolphin.core.client.ModelSynchronizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class AttributeChangeListener implements PropertyChangeListener {

    private final ClientModelStore clientModelStore;

    private final ModelSynchronizer modelSynchronizer;

    public AttributeChangeListener(ClientModelStore clientModelStore, ModelSynchronizer modelSynchronizer) {
        this.clientModelStore = clientModelStore;
        this.modelSynchronizer = modelSynchronizer;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(RemotingConstants.VALUE_NAME)) {
            if (evt.getOldValue() == null && evt.getNewValue() == null || evt.getOldValue() != null && evt.getNewValue() != null && evt.getOldValue().equals(evt.getNewValue())) {
                return;
            }

            if (isSendable(evt)) {
                modelSynchronizer.onPropertyChanged((Attribute) evt.getSource(), evt.getOldValue(), evt.getNewValue());
            }

            List<ClientAttribute> attributes = clientModelStore.findAllAttributesByQualifier(((Attribute) evt.getSource()).getQualifier());
            for (ClientAttribute attribute : attributes) {
                attribute.setValue(evt.getNewValue());
            }

        } else {
            // we assume the change is on a metadata property such as qualifier
            if (isSendable(evt)) {
                modelSynchronizer.onMetadataChanged((Attribute) evt.getSource());
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

}
