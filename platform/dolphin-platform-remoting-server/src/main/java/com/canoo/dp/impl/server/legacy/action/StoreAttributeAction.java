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
package com.canoo.dp.impl.server.legacy.action;

import com.canoo.dp.impl.remoting.legacy.commands.ChangeAttributeMetadataCommand;
import com.canoo.dp.impl.remoting.legacy.core.Attribute;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.communication.ActionRegistry;
import com.canoo.dp.impl.server.legacy.communication.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StoreAttributeAction extends DolphinServerAction {

    private static final Logger LOG = LoggerFactory.getLogger(StoreAttributeAction.class);

    public void registerIn(final ActionRegistry registry) {
        registry.register(ChangeAttributeMetadataCommand.class, new CommandHandler<ChangeAttributeMetadataCommand>() {
            @Override
            public void handleCommand(final ChangeAttributeMetadataCommand command, final List response) {
                final Attribute attribute = getServerModelStore().findAttributeById(command.getAttributeId());
                if (attribute == null) {
                    LOG.warn("Cannot find attribute with id '{}'. Metadata remains unchanged.", command.getAttributeId());
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
