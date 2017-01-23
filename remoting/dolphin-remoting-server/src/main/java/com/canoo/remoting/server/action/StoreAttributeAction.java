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
package com.canoo.remoting.server.action;

import com.canoo.communication.common.Attribute;
import com.canoo.communication.common.PresentationModel;
import com.canoo.communication.common.commands.AttributeCreatedNotification;
import com.canoo.communication.common.commands.ChangeAttributeMetadataCommand;
import com.canoo.remoting.server.ServerAttribute;
import com.canoo.remoting.server.ServerModelStore;
import com.canoo.remoting.server.ServerPresentationModel;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class StoreAttributeAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(StoreAttributeAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(AttributeCreatedNotification.class, new CommandHandler<AttributeCreatedNotification>() {
            @Override
            public void handleCommand(AttributeCreatedNotification command, List response) {
                ServerModelStore modelStore = getServerDolphin().getServerModelStore();
                Attribute existing = modelStore.findAttributeById(command.getAttributeId());
                if (existing != null) {
                    LOG.info("trying to store an already existing attribute: " + command);
                    return;
                }

                ServerAttribute attribute = new ServerAttribute(command.getPropertyName(), command.getNewValue(), command.getQualifier());
                attribute.setId(command.getAttributeId());
                PresentationModel pm = getServerDolphin().getPresentationModel(command.getPmId());
                if (pm == null) {
                    pm = new ServerPresentationModel(command.getPmId(), new ArrayList(), modelStore);
                    modelStore.add(pm);
                }
                ((ServerPresentationModel) pm)._internal_addAttribute(attribute);
                modelStore.registerAttribute(attribute);
            }
        });

        registry.register(ChangeAttributeMetadataCommand.class, new CommandHandler<ChangeAttributeMetadataCommand>() {
            @Override
            public void handleCommand(final ChangeAttributeMetadataCommand command, List response) {
                final Attribute attribute = getServerDolphin().findAttributeById(command.getAttributeId());
                if (attribute == null) {
                    LOG.warning("Cannot find attribute with id '" + command.getAttributeId() + "'. Metadata remains unchanged.");
                    return;
                }

                ((ServerAttribute) attribute).silently(new Runnable() {
                    @Override
                    public void run() {
                        if(command.getMetadataName().equals(Attribute.VALUE_NAME)) {
                            attribute.setValue(command.getValue());
                        } else if(command.getMetadataName().equals(Attribute.QUALIFIER_NAME)) {
                            if(command.getValue() == null) {
                                ((ServerAttribute) attribute).setQualifier(null);
                            } else {
                                ((ServerAttribute) attribute).setQualifier(command.getValue().toString());
                            }
                        } else {
                            throw new RuntimeException("Metadata type wrong!");
                        }
                    }

                });
            }
        });
    }

}
