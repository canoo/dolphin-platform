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
package com.canoo.dp.impl.client.legacy;

import com.canoo.dp.impl.client.legacy.communication.AbstractClientConnector;
import com.canoo.dp.impl.platform.core.Assert;
import com.canoo.dp.impl.remoting.legacy.commands.ChangeAttributeMetadataCommand;
import com.canoo.dp.impl.remoting.legacy.commands.Command;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.remoting.legacy.commands.ValueChangedCommand;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.platform.core.functional.Provider;

import java.beans.PropertyChangeEvent;

public class ModelSynchronizer {

    private final Provider<AbstractClientConnector> connectionProvider;

    public ModelSynchronizer(final Provider<AbstractClientConnector> connectionProvider) {
        this.connectionProvider = Assert.requireNonNull(connectionProvider, "connectionProvider");
    }

    public void onAdded(final ClientPresentationModel model) {
        final Command command = CreatePresentationModelCommand.makeFrom(model);
        send(command);
    }

    @Deprecated
    public void onDeleted(final ClientPresentationModel model) {
    }

    public void onPropertyChanged(final PropertyChangeEvent evt) {
        Assert.requireNonNull(evt, "evt");
        final Command command = new ValueChangedCommand(((Attribute) evt.getSource()).getId(),evt.getNewValue());
        send(command);
    }

    public void onMetadataChanged(final PropertyChangeEvent evt) {
        Assert.requireNonNull(evt, "evt");
        final Command command = new ChangeAttributeMetadataCommand(((Attribute) evt.getSource()).getId(), evt.getPropertyName(), evt.getNewValue());
        send(command);
    }

    private void send(final Command command) {
        AbstractClientConnector clientConnector = connectionProvider.get();
        if(clientConnector == null) {
            throw new IllegalStateException("No connection defined!");
        }
        clientConnector.send(command);
    }
}
