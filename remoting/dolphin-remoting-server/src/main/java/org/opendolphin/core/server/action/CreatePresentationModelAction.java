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
package org.opendolphin.core.server.action;

import org.opendolphin.RemotingConstants;
import org.opendolphin.core.comm.CreatePresentationModelCommand;
import org.opendolphin.core.server.ServerAttribute;
import org.opendolphin.core.server.ServerModelStore;
import org.opendolphin.core.server.ServerPresentationModel;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Models should be only created on the server
 */
@Deprecated
public class CreatePresentationModelAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(CreatePresentationModelAction.class.getName());

    public void registerIn(ActionRegistry registry) {

        registry.register(CreatePresentationModelCommand.class, new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(final CreatePresentationModelCommand command) {
                createPresentationModel(command, getServerModelStore());
            }
        });
    }

    private static void createPresentationModel(CreatePresentationModelCommand command, ServerModelStore serverModelStore) {
        if (serverModelStore.findPresentationModelById(command.getPmId()) != null) {
            LOG.info("Ignoring create PM '" + command.getPmId() + "' since it is already in the model store.");
            return;
        }

        if (command.getPmId().endsWith(RemotingConstants.SERVER_PM_AUTO_ID_SUFFIX)) {
            LOG.info("Creating the PM '" + command.getPmId() + "' with reserved server-auto-suffix.");
        }

        List<ServerAttribute> attributes = new LinkedList();
        for (Map<String, Object> attr : command.getAttributes()) {
            ServerAttribute attribute = new ServerAttribute((String) attr.get("propertyName"), attr.get("value"), (String) attr.get("qualifier"));
            attribute.setId((String) attr.get("id"));
            attributes.add(attribute);
        }

        ServerPresentationModel model = new ServerPresentationModel(command.getPmId(), attributes, serverModelStore);
        model.setPresentationModelType(command.getPmType());
        serverModelStore.checkClientAdded(model);
    }

}
