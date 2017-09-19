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

import com.canoo.dp.impl.remoting.legacy.RemotingConstants;
import com.canoo.dp.impl.remoting.legacy.commands.CreatePresentationModelCommand;
import com.canoo.dp.impl.server.legacy.ServerAttribute;
import com.canoo.dp.impl.server.legacy.ServerModelStore;
import com.canoo.dp.impl.server.legacy.ServerPresentationModel;
import com.canoo.dp.impl.server.legacy.communication.ActionRegistry;
import com.canoo.dp.impl.server.legacy.communication.CommandHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CreatePresentationModelAction extends DolphinServerAction {

    private static final Logger LOG = LoggerFactory.getLogger(CreatePresentationModelAction.class);

    public void registerIn(final ActionRegistry registry) {

        registry.register(CreatePresentationModelCommand.class, new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(final CreatePresentationModelCommand command, final List response) {
                createPresentationModel(command, getServerModelStore());
            }
        });
    }

    private static void createPresentationModel(final CreatePresentationModelCommand command, final ServerModelStore serverModelStore) {
        if (serverModelStore.findPresentationModelById(command.getPmId()) != null) {
            LOG.trace("Ignoring create PM '{}' since it is already in the model store.", command.getPmId());
            return;
        }

        if (command.getPmId().endsWith(RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX)) {
            LOG.trace("Creating the PM '{}' with reserved server-auto-suffix.", command.getPmId());
        }

        List<ServerAttribute> attributes = new LinkedList();
        for (Map<String, Object> attr : command.getAttributes()) {
            ServerAttribute attribute = new ServerAttribute((String) attr.get("propertyName"), attr.get("value"));
            attribute.setQualifier((String) attr.get("qualifier"));
            attribute.setId((String) attr.get("id"));
            attributes.add(attribute);
        }

        ServerPresentationModel model = new ServerPresentationModel(command.getPmId(), attributes, serverModelStore);
        model.setPresentationModelType(command.getPmType());
        serverModelStore.checkClientAdded(model);
    }

}
