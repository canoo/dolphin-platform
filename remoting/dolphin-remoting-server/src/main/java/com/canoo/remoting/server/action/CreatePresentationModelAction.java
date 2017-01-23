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

import com.canoo.communication.common.PresentationModel;
import com.canoo.communication.common.commands.CreatePresentationModelCommand;
import com.canoo.remoting.server.DefaultServerDolphin;
import com.canoo.remoting.server.ServerAttribute;
import com.canoo.remoting.server.ServerPresentationModel;
import com.canoo.remoting.server.communication.ActionRegistry;
import com.canoo.remoting.server.communication.CommandHandler;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CreatePresentationModelAction extends DolphinServerAction {

    private static final Logger LOG = Logger.getLogger(CreatePresentationModelAction.class.getName());

    public void registerIn(ActionRegistry registry) {

        registry.register(CreatePresentationModelCommand.class, new CommandHandler<CreatePresentationModelCommand>() {
            @Override
            public void handleCommand(final CreatePresentationModelCommand command, List response) {
                createPresentationModel(command, getServerDolphin());// closure wrapper for correct scoping and extracted method for static compilation
            }
        });
    }

    private static void createPresentationModel(CreatePresentationModelCommand command, DefaultServerDolphin serverDolphin) {
        if (serverDolphin.getPresentationModel(command.getPmId()) != null) {
            LOG.info("Ignoring create PM '" + command.getPmId() + "' since it is already in the model store.");
            return;
        }

        if (command.getPmId().endsWith(ServerPresentationModel.AUTO_ID_SUFFIX)) {
            LOG.info("Creating the PM '" + command.getPmId() + "' with reserved server-auto-suffix.");
        }

        List<ServerAttribute> attributes = new LinkedList();
        for (Map<String, Object> attr : command.getAttributes()) {
            ServerAttribute attribute = new ServerAttribute((String) attr.get("propertyName"), attr.get("value"), (String) attr.get("qualifier"));
            attribute.setId((String) attr.get("id"));
            attributes.add(attribute);
        }

        PresentationModel model = new ServerPresentationModel(command.getPmId(), attributes, serverDolphin.getServerModelStore());
        ((ServerPresentationModel) model).setPresentationModelType(command.getPmType());
        if (serverDolphin.getServerModelStore().containsPresentationModel(model.getId())) {
            LOG.info("There already is a PM with id " + model.getId() + ". Create PM ignored.");
        } else {
            serverDolphin.getServerModelStore().add(model);
        }
    }

}
