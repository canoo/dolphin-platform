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

import com.canoo.communication.common.commands.ValueChangedCommand;
import com.canoo.remoting.server.ServerAttribute;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;

import java.util.List;
import java.util.logging.Logger;

public class StoreValueChangeAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(StoreValueChangeAction.class.getName());

    public void registerIn(ActionRegistry registry) {
        registry.register(ValueChangedCommand.class, new CommandHandler<ValueChangedCommand>() {
            @Override
            public void handleCommand(final ValueChangedCommand command, List response) {
                final ServerAttribute attribute = getServerDolphin().findAttributeById(command.getAttributeId());
                if (attribute != null) {
                    if ((attribute.getValue() != null || command.getOldValue() != null) && !attribute.getValue().equals(command.getOldValue())) {
                        LOG.warning("S: updating attribute with id '" + command.getAttributeId() + "' to new value '" + command.getNewValue() + "' even though its old command value '" + command.getOldValue() + "' does not conform to the old value of '" + attribute.getValue() + "'. Client overrules server.");
                    }

                    attribute.silently(new Runnable() {
                        @Override
                        public void run() {
                            attribute.setValue(command.getNewValue());
                        }

                    });
                } else {
                    LOG.severe("S: cannot find attribute with id '" + command.getAttributeId() + "' to change value from '" + command.getOldValue() + "' to '" + command.getNewValue() + "'.");
                }
            }
        });
    }
}
