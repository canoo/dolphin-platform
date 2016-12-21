/*
 * Copyright 2015-2016 Canoo Engineering AG.
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

import org.opendolphin.core.PresentationModel;
import org.opendolphin.core.comm.DeletedPresentationModelNotification;
import org.opendolphin.core.server.comm.ActionRegistry;
import org.opendolphin.core.server.comm.CommandHandler;

import java.util.List;

public class DeletePresentationModelAction extends DolphinServerAction {

    public void registerIn(ActionRegistry registry) {
        registry.register(DeletedPresentationModelNotification.class, new CommandHandler<DeletedPresentationModelNotification>() {
            @Override
            public void handleCommand(final DeletedPresentationModelNotification command, List response) {
                PresentationModel model = getServerDolphin().getPresentationModel(command.getPmId());

                // Note: we cannot do serverDolphin.remove(model) since that may trigger another DeleteCommand
                // We need to do it silently just like when creating PMs.

                getServerDolphin().getServerModelStore().remove(model);
            }
        });
    }

}
